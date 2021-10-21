package ca.bc.gov.bchealth.http

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

class ReceivedCookiesInterceptor constructor(cookies: CookieStorage) : Interceptor {

    var _storage: CookieStorage = cookies

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            val cookies = HashSet<String>()
            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
            }
            _storage.store(cookies)
        }
        if (originalResponse.priorResponse != null && !originalResponse.priorResponse!!
            .headers("Set-Cookie")
            .isEmpty()
        ) {
            val cookies = HashSet<String>()
            for (header in originalResponse.priorResponse!!.headers("Set-Cookie")) {
                cookies.add(header)
            }
            _storage.store(cookies)
        }
        return originalResponse
    }
}
