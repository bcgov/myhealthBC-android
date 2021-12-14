package ca.bc.gov.data.remote.interceptor

import ca.bc.gov.data.utils.CookieStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class CookiesInterceptor @Inject constructor(
    private val cookieStorage: CookieStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        cookieStorage.getCookies()?.forEach { cookie ->
            requestBuilder.addHeader("Cookie", cookie)
        }

        val response = chain.proceed(requestBuilder.build())
        val responseHeaders = response.headers("Set-Cookie")
        val cookies = hashSetOf<String>()
        responseHeaders.forEach { header ->
            cookies.add(header)
        }
        if (cookies.isNotEmpty()) {
            cookieStorage.store(cookies)
        }

        //CHECK FOR PRIOR RESPONSE
        val priorResponseHeaders = response.priorResponse?.headers("Set-Cookie")
        val priorCookies = hashSetOf<String>()
        priorResponseHeaders?.forEach { header ->
            priorCookies.add(header)
        }
        if (priorCookies.isNotEmpty()) {
            cookieStorage.store(priorCookies)
        }
        return response
    }
}