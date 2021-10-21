package ca.bc.gov.bchealth.http

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AddCookiesInterceptor constructor(cookies: CookieStorage) : Interceptor {

    var _storage: CookieStorage? = cookies

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        val preferences: HashSet<String>? = _storage?.getCookies()
        if (preferences != null) {
            for (cookie in preferences) {
                builder.addHeader("Cookie", cookie)
                Log.v(
                    "OkHttp",
                    "Adding Header: $cookie"
                ) // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
            }
        }
        return chain.proceed(builder.build())
    }
}
