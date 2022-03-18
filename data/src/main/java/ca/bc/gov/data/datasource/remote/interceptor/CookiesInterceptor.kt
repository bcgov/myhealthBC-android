package ca.bc.gov.data.datasource.remote.interceptor

import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class CookiesInterceptor @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        preferenceStorage.cookies?.forEach { cookie ->
            requestBuilder.addHeader("Cookie", cookie)
        }

        return chain.proceed(requestBuilder.build())
    }
}
