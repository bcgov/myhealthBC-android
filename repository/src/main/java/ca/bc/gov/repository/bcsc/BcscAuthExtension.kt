package ca.bc.gov.repository.bcsc

import android.content.Context
import android.net.Uri
import ca.bc.gov.common.const.AUTH_ERROR
import ca.bc.gov.common.const.AUTH_ERROR_DO_LOGIN
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthState
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationException
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationResponse
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationService
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationServiceConfiguration
import ca.bc.gov.repository.library.java.net.openid.appauth.TokenResponse
import kotlinx.coroutines.suspendCancellableCoroutine
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

    val request = authorizationResponse.createTokenExchangeRequest()
    println("svn: request json: " + request.jsonSerializeString()
        .replace("{", "\nsvn: request json: {")
        .replace("[", "\nsvn: request json: [")
    )

    AuthorizationService(applicationContext).performTokenRequest(
        request
    ) { response, ex ->

        println("svn: awaitPerformTokenRequest response: " + response)
        println("svn: awaitPerformTokenRequest ex:" + ex)

        println("svn: ###################################################")
        println("svn: ####### log ended ############################################")
        println("svn: ###################################################")
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
            continuation.resumeWithException(
                MyHealthException(AUTH_ERROR_DO_LOGIN, "Login check failed!")
            )
        } else {
            authService.dispose()
            continuation.resume(accessToken)
        }
    }
}
