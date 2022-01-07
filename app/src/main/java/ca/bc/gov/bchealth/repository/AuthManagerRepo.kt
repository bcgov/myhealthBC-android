package ca.bc.gov.bchealth.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.ResponseTypeValues

/*
* @author amit_metri on 05,January,2022
*/
class AuthManagerRepo(
    private val appContext: Context,
    private val dataStoreRepo: DataStoreRepo
) {

    companion object {

        private const val ISSUER_END_POINT = "https://dev.oidc.gov.bc.ca/auth/realms/ff09qn3f"

        private const val CLIENT_ID = "myhealthapp"

        private const val REDIRECT_URI = "myhealthbc://*"

        private val params = mapOf("kc_idp_hint" to "bcsc")

        private const val SCOPE = "openid email profile"

        private const val PROMPT = "login"
    }

    private lateinit var authState: AuthState

    private lateinit var authService: AuthorizationService

    private lateinit var authServiceConfiguration: AuthorizationServiceConfiguration

    /*
    * Used to manage Success, Error and Loading status in the UI
    * */
    private val uiStateMutableSharedFlow =
        MutableSharedFlow<Response<String>>(extraBufferCapacity = 1)
    val uiStateSharedFlow: SharedFlow<Response<String>>
        get() = uiStateMutableSharedFlow.asSharedFlow()

    /*
    * Used to communicate logged in or logged out status
    * */
    private val loginMutableSharedFlow = MutableSharedFlow<Boolean>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val loginSharedFlow: SharedFlow<Boolean>
        get() = loginMutableSharedFlow

    fun initializeLogin(
        authResultLauncher: ActivityResultLauncher<Intent>,
        requireContext: Context
    ) {

        uiStateMutableSharedFlow.tryEmit(Response.Loading())

        AuthorizationServiceConfiguration.fetchFromIssuer(
            Uri.parse(ISSUER_END_POINT)
        ) { serviceConfig, exception ->

            if (exception != null || serviceConfig == null) {
                throw Exception(exception?.message)
            }

            authServiceConfiguration = serviceConfig

            authState = AuthState(authServiceConfiguration)

            obtainAuthCode(authServiceConfiguration, authResultLauncher, requireContext)
        }
    }

    private fun obtainAuthCode(
        authServiceConfiguration: AuthorizationServiceConfiguration,
        resultLauncher: ActivityResultLauncher<Intent>,
        requireContext: Context
    ) {

        val authRequestBuilder =
            AuthorizationRequest.Builder(
                authServiceConfiguration, // the authorization service configuration
                CLIENT_ID, // the client ID, typically pre-registered and static
                ResponseTypeValues.CODE, // the response_type value: we want a code
                Uri.parse(REDIRECT_URI) // the redirect URI to which the auth response is sent
            )

        val authorizationRequest = authRequestBuilder
            .setScope(SCOPE)
            .setPrompt(PROMPT)
            .setAdditionalParameters(params)
            .build()

        authService = AuthorizationService(requireContext)

        val authIntent = authService.getAuthorizationRequestIntent(authorizationRequest)
        resultLauncher.launch(authIntent)
    }

    fun processAuthResponse(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {

            val data: Intent? = activityResult.data
            val authorizationResponse = data?.let { AuthorizationResponse.fromIntent(it) }
            val ex = AuthorizationException.fromIntent(data)

            authState.update(authorizationResponse, ex)

            if (ex != null) {
                uiStateMutableSharedFlow.tryEmit(Response.Error(ErrorData.GENERIC_ERROR))
                throw Exception(ex.message)
            } else {
                authorizationResponse?.let { performTokenRequest(it) }
            }
        } else {
            uiStateMutableSharedFlow.tryEmit(Response.Error(ErrorData.GENERIC_ERROR))
            throw Exception(appContext.resources.getString(R.string.incorrect_credentials))
        }
    }

    private fun performTokenRequest(authorizationResponse: AuthorizationResponse) {

        authService.performTokenRequest(
            authorizationResponse.createTokenExchangeRequest()
        ) { resp, ex ->
            if (ex != null) {
                uiStateMutableSharedFlow.tryEmit(Response.Error(ErrorData.GENERIC_ERROR))
                throw Exception(ex.message)
            }

            authState.update(resp, ex)
            dataStoreRepo.setAuthState(authState)
            uiStateMutableSharedFlow.tryEmit(Response.Success())
        }
    }

    /*
    * Check BCSC login
    * */
    suspend fun checkLogin(
        destinationId: Int,
        navOptions: NavOptions,
        navController: NavController
    ) {

        dataStoreRepo.getAuthState.collect { authState ->

            if (authState == null) {
                navigateToLoginFragment(destinationId, navOptions, navController)
                return@collect
            }

            authState.performActionWithFreshTokens(AuthorizationService(appContext)) { accessToken, idToken, ex ->

                if (ex != null) {
                    navigateToLoginFragment(destinationId, navOptions, navController)
                    return@performActionWithFreshTokens
                }

                if (!accessToken.isNullOrEmpty() && !idToken.isNullOrEmpty()) {

                    dataStoreRepo.setAuthState(authState)

                    // navigate to destination
                    navController.navigate(destinationId, null, navOptions)
                } else {
                    navigateToLoginFragment(destinationId, navOptions, navController)
                }
            }
        }
    }

    /*
    * Look for profile if user is logged in
    * */
    suspend fun checkProfile() {

        dataStoreRepo.getAuthState.collect { authState ->

            if (authState == null) {
                loginMutableSharedFlow.tryEmit(false)
                return@collect
            }

            authState.performActionWithFreshTokens(AuthorizationService(appContext)) { accessToken, idToken, ex ->

                if (ex != null) {
                    loginMutableSharedFlow.tryEmit(false)
                    return@performActionWithFreshTokens
                }

                if (!accessToken.isNullOrEmpty() && !idToken.isNullOrEmpty()) {
                    dataStoreRepo.setAuthState(authState)
                    loginMutableSharedFlow.tryEmit(true)
                } else {
                    loginMutableSharedFlow.tryEmit(false)
                }
            }
        }
    }

    private fun navigateToLoginFragment(
        destinationId: Int,
        navOptions: NavOptions,
        navController: NavController
    ) {
        val bundle = Bundle()
        bundle.putInt("destinationId", destinationId)
        navController.navigate(R.id.loginFragment, bundle, navOptions)
    }

    /*
    * BCSC logout
    * */
    suspend fun logout(
        logoutResultLauncher: ActivityResultLauncher<Intent>
    ) {
        dataStoreRepo.getAuthState.collect { authState ->

            authState?.let {
                val endSessionRequest =
                    authState.authorizationServiceConfiguration?.let { it1 ->
                        EndSessionRequest.Builder(it1)
                            .setIdTokenHint(authState.lastTokenResponse?.idToken)
                            .setPostLogoutRedirectUri(REDIRECT_URI.toUri())
                            .build()
                    }

                val endSessionIntent = endSessionRequest?.let { it1 ->
                    AuthorizationService(appContext).getEndSessionRequestIntent(
                        it1
                    )
                }

                logoutResultLauncher.launch(endSessionIntent)
            }
        }
    }

    fun processLogoutResponse() {
        dataStoreRepo.setAuthState(AuthState())
    }
}
