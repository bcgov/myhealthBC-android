package ca.bc.gov.data.datasource.remote.interceptor

import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class ReceivedCookieInterceptor @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val responseHeaders = response.headers("Set-Cookie")
        val cookies = hashSetOf<String>()
        responseHeaders.forEach { header ->
            cookies.add(header)
        }
        if (cookies.isNotEmpty()) {
            preferenceStorage.cookies = cookies
        }

        // CHECK FOR PRIOR RESPONSE
        val priorResponseHeaders = response.priorResponse?.headers("Set-Cookie")
        val priorCookies = hashSetOf<String>()
        priorResponseHeaders?.forEach { header ->
            priorCookies.add(header)
        }
        if (priorCookies.isNotEmpty()) {
            preferenceStorage.cookies = priorCookies
        }

        return response
    }
}
