package ca.bc.gov.data.datasource.remote.interceptor

import android.content.Context
import ca.bc.gov.common.exceptions.MyHealthAuthException
import ca.bc.gov.data.datasource.remote.interceptor.HeaderInterceptor.Companion.AUTHORIZATION
import ca.bc.gov.preference.EncryptedPreferenceStorage
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthState
import net.openid.appauth.extension.getBCSCAuthData
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 * [HeaderInterceptor] will check for the header in the api call,
 * If api call is missing [AUTHORIZATION] in the header then it will get added.
 *
 * Currently API v2 is not going to send the [AUTHORIZATION] in the request
 * and eventually all other API will follow this pattern.
 */
class HeaderInterceptor @Inject constructor(
    private val context: Context,
    private val preferenceStorage: EncryptedPreferenceStorage
) : Interceptor {
    // todo: refactor OKHTTP client to exclude public apis
    private val urlExcludedFromCheck = listOf(
        "api/immunizationservice/PublicVaccineStatus",
        "/gatewayapiservice/Communication/Mobile"
    )

    companion object {
        private const val AUTHORIZATION = "Authorization"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (urlExcludedFromCheck.any { originalRequest.url.toString().contains(it, true) }) {
            return chain.proceed(originalRequest)
        }

        val originalHeaders = originalRequest.headers

        if (!originalHeaders.names().contains(AUTHORIZATION)) {
            val authState: String =
                preferenceStorage.authState ?: throw MyHealthAuthException("HEADER")
            val authStateResult =
                AuthState.jsonDeserialize(authState) ?: throw MyHealthAuthException("HEADER")
            val bcscAuthDataDto = runBlocking {
                getBCSCAuthData(context, authStateResult)
            }

            val updatedRequest = originalRequest.newBuilder().apply {
                addHeader(AUTHORIZATION, bcscAuthDataDto.authToken)
            }.build()
            return chain.proceed(updatedRequest)
        }

        return chain.proceed(originalRequest)
    }
}
