package ca.bc.gov.data.datasource.remote.interceptor

import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

/**
 * @author Pinakin Kansara
 */
class UserAgentInterceptor(
    private val appName: String,
    private val appVersion: String
) : Interceptor {

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

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
            .header("User-Agent", userAgent)
        return chain.proceed(requestBuilder.build())
    }
}
