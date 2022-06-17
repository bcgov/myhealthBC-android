package ca.bc.gov.data.datasource.remote.interceptor

import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.URL
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 24,May,2022
 */

class HostSelectionInterceptor @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val baseUrl = URL(preferenceStorage.baseUrl)
        val request = chain.request()
        val newUrl = request.url.newBuilder().host(baseUrl.host).build()
        val newRequest = request.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}
