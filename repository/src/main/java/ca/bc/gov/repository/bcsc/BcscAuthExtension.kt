package ca.bc.gov.repository.bcsc

import android.content.Context
import android.net.Uri
import ca.bc.gov.common.const.AUTH_ERROR
import ca.bc.gov.common.const.AUTH_ERROR_DO_LOGIN
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.repository.library.java.net.openid.appauth.AppAuthConfiguration
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthState
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationException
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationResponse
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationService
import ca.bc.gov.repository.library.java.net.openid.appauth.AuthorizationServiceConfiguration
import ca.bc.gov.repository.library.java.net.openid.appauth.TokenResponse
import ca.bc.gov.repository.library.java.net.openid.appauth.browser.BrowserAllowList
import ca.bc.gov.repository.library.java.net.openid.appauth.browser.BrowserDenyList
import ca.bc.gov.repository.library.java.net.openid.appauth.browser.Browsers
import ca.bc.gov.repository.library.java.net.openid.appauth.browser.VersionRange
import ca.bc.gov.repository.library.java.net.openid.appauth.browser.VersionedBrowserMatcher
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

    val appauthConfiguration = AppAuthConfiguration.Builder()
        .setBrowserMatcher(
            BrowserAllowList(
                VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
            )
        )
        .setBrowserMatcher(
            BrowserDenyList(
                VersionedBrowserMatcher(
                    Browsers.SBrowser.PACKAGE_NAME,
                    Browsers.SBrowser.SIGNATURE_SET,
                    true,
                    VersionRange.atMost("5.3")
                )
            )
        )
        .build()
    AuthorizationService(applicationContext, appauthConfiguration).performTokenRequest(
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
    val appauthConfiguration = AppAuthConfiguration.Builder()
        .setBrowserMatcher(
            BrowserAllowList(
                VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
            )
        )
        .setBrowserMatcher(
            BrowserDenyList(
                VersionedBrowserMatcher(
                    Browsers.SBrowser.PACKAGE_NAME,
                    Browsers.SBrowser.SIGNATURE_SET,
                    true,
                    VersionRange.atMost("5.3")
                )
            )
        )
        .build()
    val authService = AuthorizationService(applicationContext, appauthConfiguration)
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
