package net.openid.appauth.extension

import android.content.Context
import android.net.Uri
import android.util.Base64
import ca.bc.gov.common.exceptions.MyHealthAuthException
import ca.bc.gov.common.model.BCSCAuthDataDto
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.TokenResponse
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val HDID = "hdid"
private const val BEARER = "Bearer"

suspend fun awaitFetchFromIssuer(uri: Uri): AuthorizationServiceConfiguration =
    suspendCancellableCoroutine { continuation ->
        AuthorizationServiceConfiguration.fetchFromIssuer(
            uri
        ) { serviceConfiguration, ex ->

            if (serviceConfiguration == null) {
                ex?.let {
                    continuation.resumeWithException(it)
                }
            } else {
                continuation.resume(serviceConfiguration)
            }
        }
    }

suspend fun awaitPerformTokenRequest(
    applicationContext: Context,
    authorizationResponse: AuthorizationResponse
): Pair<TokenResponse, AuthorizationException?> =
    suspendCancellableCoroutine { continuation ->

        AuthorizationService(applicationContext).performTokenRequest(
            authorizationResponse.createTokenExchangeRequest()
        ) { response, ex ->
            if (response == null) {
                ex?.let { continuation.resumeWithException(it) }
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
        if (accessToken == null || idToken == null) {
            ex?.let { continuation.resumeWithException(it) }
        } else {
            authService.dispose()
            continuation.resume(accessToken)
        }
    }
}

suspend fun getBCSCAuthData(applicationContext: Context, authState: AuthState): BCSCAuthDataDto {
    val accessToken = awaitPerformActionWithFreshTokens(applicationContext, authState)
    val json = decodeAccessToken(accessToken)
    val hdId = json.get(HDID).toString()
    if (hdId.isEmpty())
        throw MyHealthAuthException("Invalid access token!")
    else
        return BCSCAuthDataDto(BEARER.plus(" ").plus(accessToken), hdId)
}

@Throws(Exception::class)
private fun decodeAccessToken(JWTEncodedAccessToken: String): JSONObject {
    if (JWTEncodedAccessToken.isEmpty()) {
        throw MyHealthAuthException("Invalid access token!")
    }
    val split = JWTEncodedAccessToken.split("\\.".toRegex()).toTypedArray()
    return JSONObject(getJson(split[1]))
}

@Throws(UnsupportedEncodingException::class)
private fun getJson(strEncoded: String): String {
    val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
    return String(decodedBytes, Charset.defaultCharset())
}
