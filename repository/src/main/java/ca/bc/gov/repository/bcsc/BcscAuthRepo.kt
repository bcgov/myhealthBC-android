package ca.bc.gov.repository.bcsc

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import ca.bc.gov.common.const.AUTH_ERROR
import ca.bc.gov.common.const.AUTH_ERROR_DO_LOGIN
import ca.bc.gov.common.const.MUST_CALL_MOBILE_CONFIG
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.preference.EncryptedPreferenceStorage
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.ResponseTypeValues
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.time.Instant

/*
* @author amit_metri on 05,January,2022
*/
const val BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME = "BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME"

class BcscAuthRepo(
    private val applicationContext: Context,
    private val encryptedPreferenceStorage: EncryptedPreferenceStorage,
    private val patientLocalDataSource: PatientLocalDataSource,
) {

    private lateinit var authState: AuthState
    private lateinit var authService: AuthorizationService
    private lateinit var authServiceConfiguration: AuthorizationServiceConfiguration

    private fun setAuthState(authState: AuthState?) {
        if (authState != null) {
            authState.lastTokenResponse?.additionalParameters?.get("refresh_expires_in")?.toLong()
                ?.let {
                    encryptedPreferenceStorage.sessionTime = Instant.now().epochSecond.plus(it)
                }
        } else {
            encryptedPreferenceStorage.sessionTime = -1L
        }
        encryptedPreferenceStorage.authState = authState?.jsonSerializeString()
    }

    private fun getAuthState(): AuthState? {
        return encryptedPreferenceStorage.authState?.let {
            AuthState.jsonDeserialize(
                it
            )
        }
    }

    /*
    * Initiate login using AppAuth library
    * returns Intent which is required to launch web chrome client for authentication
    * */
    suspend fun initiateLogin(): Intent {
        val authenticationEndpoint = encryptedPreferenceStorage.authenticationEndpoint
            ?: throw MyHealthException(MUST_CALL_MOBILE_CONFIG)

        val clientId = encryptedPreferenceStorage.clientId
            ?: throw MyHealthException(MUST_CALL_MOBILE_CONFIG)

        val identityProviderId = encryptedPreferenceStorage.identityProviderId
            ?: throw MyHealthException(MUST_CALL_MOBILE_CONFIG)

        val serviceConfig = awaitFetchFromIssuer(Uri.parse(authenticationEndpoint))
        authServiceConfiguration = serviceConfig
        authState = AuthState(authServiceConfiguration)
        val authRequestBuilder =
            AuthorizationRequest.Builder(
                authServiceConfiguration, // the authorization service configuration
                clientId, // the client ID, typically pre-registered
                ResponseTypeValues.CODE, // the response_type value: we want a code
                Uri.parse(REDIRECT_URI) // the redirect URI to which the auth response is sent
            )

        val params = mapOf("kc_idp_hint" to identityProviderId)
        val authorizationRequest = authRequestBuilder
            .setScope(SCOPE)
            .setPrompt(PROMPT)
            .setAdditionalParameters(params)
            .build()

        authService = AuthorizationService(applicationContext)
        return authService.getAuthorizationRequestIntent(authorizationRequest)
    }

    /*
    * User auth code to get access token and update the AuthState
    * */
    suspend fun processAuthResponse(data: Intent?): Boolean {

        data ?: throw MyHealthException(AUTH_ERROR, "Login failed!")
        val authorizationResponse = AuthorizationResponse.fromIntent(data)
        val ex = AuthorizationException.fromIntent(data)
        authorizationResponse ?: throw MyHealthException(AUTH_ERROR, "Login failed!")
        authState.update(authorizationResponse, ex)
        val tokenResponse = awaitPerformTokenRequest(applicationContext, authorizationResponse)
        authState.update(tokenResponse.first, tokenResponse.second)
        setAuthState(authState)
        authService.dispose()
        return true
    }

    /*
    * Check for logged in session
    * */
    fun checkSession(): Boolean {
        val authState = getAuthState() ?: return false
        return !authState.needsTokenRefresh
    }

    /*
    * BCSC Logout
    * */
    fun getEndSessionIntent(): Intent {

        val authState = getAuthState() ?: throw MyHealthException(AUTH_ERROR, "Logout failed!")
        val authServiceConfig = authState.authorizationServiceConfiguration
            ?: throw MyHealthException(AUTH_ERROR, "Logout failed!")
        val endSessionRequest = EndSessionRequest.Builder(authServiceConfig)
            .setIdTokenHint(authState.lastTokenResponse?.idToken)
            .setPostLogoutRedirectUri(REDIRECT_URI.toUri())
            .build()

        return AuthorizationService(applicationContext)
            .getEndSessionRequestIntent(
                endSessionRequest
            )
    }

    suspend fun processLogoutResponse() {
        patientLocalDataSource.deleteDependentPatients()
        patientLocalDataSource.deleteBcscAuthenticatedPatientData()
        setAuthState(null)
    }

    suspend fun getAuthParametersDto(): AuthParametersDto {
        val pair: Pair<String, String> = getAuthParameters()
        return AuthParametersDto(
            token = pair.first,
            hdid = pair.second,
        )
    }

    private suspend fun getAuthParameters(): Pair<String, String> {
        val authState =
            getAuthState() ?: throw MyHealthException(AUTH_ERROR_DO_LOGIN, "Login again!")
        val accessToken = awaitPerformActionWithFreshTokens(applicationContext, authState)
        val json = decodeAccessToken(accessToken)
        val hdId = json.get(HDID).toString()
        if (hdId.isEmpty())
            throw MyHealthException(AUTH_ERROR_DO_LOGIN, "Invalid access token!")
        else
            return Pair(BEARER.plus(" ").plus(accessToken), hdId)
    }

    @Throws(Exception::class)
    private fun decodeAccessToken(JWTEncodedAccessToken: String): JSONObject {
        if (JWTEncodedAccessToken.isEmpty()) {
            throw MyHealthException(AUTH_ERROR_DO_LOGIN, "Invalid access token!")
        }
        val split = JWTEncodedAccessToken.split("\\.".toRegex()).toTypedArray()
        return JSONObject(getJson(split[1]))
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charset.defaultCharset())
    }

    fun setPostLoginCheck(postLoginCheck: PostLoginCheck) {
        encryptedPreferenceStorage.postLoginCheck = postLoginCheck.name
    }

    fun getPostLoginCheck(): String? {
        return encryptedPreferenceStorage.postLoginCheck
    }

    companion object {
        const val REDIRECT_URI = "myhealthbc://*"
        private const val SCOPE = "openid email profile"
        private const val PROMPT = "login"
        private const val HDID = "hdid"
        private const val NAME = "name"
        private const val BEARER = "Bearer"
    }
}

enum class PostLoginCheck {
    IN_PROGRESS,
    COMPLETE
}
