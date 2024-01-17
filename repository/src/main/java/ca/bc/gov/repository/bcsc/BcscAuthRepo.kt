package ca.bc.gov.repository.bcsc

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import ca.bc.gov.common.const.AUTH_ERROR
import ca.bc.gov.common.const.AUTH_ERROR_DO_LOGIN
import ca.bc.gov.common.const.MUST_CALL_MOBILE_CONFIG
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.UserAuthenticationStatus
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.preference.EncryptedPreferenceStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.extension.awaitFetchFromIssuer
import net.openid.appauth.extension.awaitPerformTokenRequest
import net.openid.appauth.extension.getBCSCAuthData
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

    val userAuthenticationStatus: Flow<UserAuthenticationStatus> = flow {
        while (true) {
            val authString = encryptedPreferenceStorage.authState
            authString?.let {
                val authState = AuthState.jsonDeserialize(it)
                if (authState.isAuthorized) {
                    if (authState.needsTokenRefresh) {
                        emit(UserAuthenticationStatus.SESSION_TIME_OUT)
                    } else {
                        emit(UserAuthenticationStatus.AUTHENTICATED)
                    }
                } else {
                    emit(UserAuthenticationStatus.UN_AUTHENTICATED)
                }
            } ?: emit(UserAuthenticationStatus.UN_AUTHENTICATED)
            delay(100)
        }
    }

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
        val authState =
            getAuthState() ?: throw MyHealthException(AUTH_ERROR_DO_LOGIN, "Login again!")
        val bcscAuthDataDto = getBCSCAuthData(applicationContext, authState)
        return AuthParametersDto(
            token = bcscAuthDataDto.authToken,
            hdid = bcscAuthDataDto.hdId,
        )
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
    }
}

enum class PostLoginCheck {
    IN_PROGRESS,
    COMPLETE
}
