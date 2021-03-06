package ca.bc.gov.repository.bcsc

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ca.bc.gov.common.const.AUTH_ERROR
import ca.bc.gov.common.const.AUTH_ERROR_DO_LOGIN
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.utils.titleCase
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.repository.R
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthState
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationException
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationRequest
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationResponse
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationService
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationServiceConfiguration
import ca.bc.gov.repository.library.java.net.openid.appauth.EndSessionRequest
import ca.bc.gov.repository.library.java.net.openid.appauth.ResponseTypeValues
import ca.bc.gov.repository.worker.FetchAuthenticatedHealthRecordsWorker
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
        return encryptedPreferenceStorage.authState?.let { AuthState.jsonDeserialize(it) }
    }

    /*
    * Initiate login using AppAuth library
    * returns Intent which is required to launch web chrome client for authentication
    * */
    suspend fun initiateLogin(): Intent {

        val serviceConfig = awaitFetchFromIssuer(
            Uri.parse(applicationContext.getString(R.string.auth_issuer_end_point))
        )
        authServiceConfiguration = serviceConfig
        authState = AuthState(authServiceConfiguration)
        val authRequestBuilder =
            AuthorizationRequest.Builder(
                authServiceConfiguration, // the authorization service configuration
                applicationContext.getString(R.string.client_id), // the client ID, typically pre-registered and static
                ResponseTypeValues.CODE, // the response_type value: we want a code
                Uri.parse(REDIRECT_URI) // the redirect URI to which the auth response is sent
            )

        val params = mapOf("kc_idp_hint" to applicationContext.getString(R.string.kc_idp_hint))
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
        patientLocalDataSource.deleteBcscAuthenticatedPatientData()
        setAuthState(null)
    }

    suspend fun getAuthParameters(): Pair<String, String> {
        val authState = getAuthState() ?: throw MyHealthException(AUTH_ERROR_DO_LOGIN, "Login again!")
        val accessToken = awaitPerformActionWithFreshTokens(applicationContext, authState)
        val json = decodeAccessToken(accessToken)
        val hdId = json.get(HDID).toString()
        if (hdId.isEmpty())
            throw MyHealthException(AUTH_ERROR_DO_LOGIN, "Invalid access token!")
        else
            return Pair(BEARER.plus(" ").plus(accessToken), hdId)
    }

    suspend fun getUserName(): String {
        var userName = "Not available!"
        try {
            val authState = getAuthState() ?: throw MyHealthException(AUTH_ERROR, "Login again!")
            val json = authState.accessToken?.let { decodeAccessToken(it) }
            userName = json?.get(NAME).toString().titleCase()
        } catch (e: java.lang.Exception) {
            // NA
        }
        return userName
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

    fun executeOneTimeDatFetch() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWorkRequest =
            OneTimeWorkRequest.Builder(FetchAuthenticatedHealthRecordsWorker::class.java)
                .setConstraints(constraints)
                .build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniqueWork(
            BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
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
