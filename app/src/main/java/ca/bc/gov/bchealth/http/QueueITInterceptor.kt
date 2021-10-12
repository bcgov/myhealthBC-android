package ca.bc.gov.bchealth.http

import ca.bc.gov.bchealth.di.ApiClientModule
import java.io.IOException
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class QueueITInterceptor(
    private var _cookies: CookieStorage
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val urlBuilder: HttpUrl.Builder = chain.request().url.newBuilder()

        if (ApiClientModule.token.isNotEmpty()) {
            urlBuilder.addQueryParameter("queueittoken", ApiClientModule.token)
        }

        val chainReq: Request = chain.request()
        val req: Request = chainReq.newBuilder()
            .addHeader("x-queueit-ajaxpageurl", chainReq.url.toString())
            .url(urlBuilder.build())
            .build()
        val res: Response = chain.proceed(req)

        if (mustQueue(res)) {
            ApiClientModule.token = ""
            _cookies.clear()
            val resHeaders = res.headers
            throw MustBeQueued(resHeaders["x-queueit-redirect"])
        }
        return res
    }


    fun mustQueue(response: Response): Boolean {
        val responseHeaders = response.headers
        return responseHeaders.names().contains("x-queueit-redirect")
    }
}
