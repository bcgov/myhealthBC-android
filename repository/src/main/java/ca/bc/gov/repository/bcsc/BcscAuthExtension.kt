package ca.bc.gov.repository.bcsc

import android.content.Context
import android.net.Uri
import ca.bc.gov.common.const.AUTH_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.TokenResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/*
* Created by amit_metri on 31,January,2022
*/

suspend fun awaitFetchFromIssuer(uri: Uri): AuthorizationServiceConfiguration =
    suspendCancellableCoroutine { continuation ->

        AuthorizationServiceConfiguration.fetchFromIssuer(
            uri
        ) { serviceConfiguration, ex ->

            if (ex != null || serviceConfiguration == null) {
                continuation.resumeWithException(MyHealthException(AUTH_ERROR, "Login failed!"))
            } else {
                continuation.resume(serviceConfiguration)
            }
        }
    }

suspend fun awaitPerformTokenRequest(
    applicationContext: Context,
    authorizationResponse: AuthorizationResponse
): Pair<TokenResponse, AuthorizationException?> = suspendCancellableCoroutine { continuation ->

    AuthorizationService(applicationContext).performTokenRequest(
        authorizationResponse.createTokenExchangeRequest()
    ) { response, ex ->
        if (ex != null || response == null) {
            continuation.resumeWithException(MyHealthException(AUTH_ERROR, "Login failed!"))
        } else {
            continuation.resume(Pair(response, ex))
        }
    }
}

suspend fun awaitPerformActionWithFreshTokens(
    applicationContext: Context,
    authState: AuthState
): String = suspendCancellableCoroutine { continuation ->
    val authService = AuthorizationService(applicationContext)
    authState.performActionWithFreshTokens(
        authService
    ) { accessToken, idToken, ex ->
        if (accessToken == null || idToken == null || ex != null) {
            continuation.resumeWithException(MyHealthException(AUTH_ERROR, "Login check failed!"))
        } else {
            authService.dispose()
            continuation.resume(accessToken)
        }
    }
}
