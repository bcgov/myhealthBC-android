package ca.bc.gov.bchealth.http

import android.os.Build
import java.io.IOException
import java.util.Locale
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class UserAgentInterceptor constructor(appName: String, appVersion: String) : Interceptor {

    var userAgent = String.format(
        Locale.US,
        "%s/%s (Android %s; %s; %s %s; %s)",
        appName,
        appVersion,
        Build.VERSION.RELEASE,
        Build.MODEL,
        Build.BRAND,
        Build.DEVICE,
        Locale.getDefault().language

    )

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentRequest: Request = chain.request()
            .newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(userAgentRequest)
    }
}
