package ca.bc.gov.data.remote.interceptor

import ca.bc.gov.common.const.MUST_QUEUED
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.data.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.utils.CookieStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class QueueItInterceptor @Inject constructor(
    private val cookieStorage: CookieStorage,
    private val preferenceStorage: EncryptedPreferenceStorage
) : Interceptor {

    companion object {
        private const val TAG = "QueueItInterceptor"
        private const val HEADER_QUEUE_IT_TOKEN = "queueittoken"
        private const val HEADER_QUEUE_IT_AJAX_URL = "x-queueit-ajaxpageurl"
        private const val HEADER_QUEUE_IT_REDIRECT_URL = "x-queueit-redirect"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestUrlBuilder = chain.request().url.newBuilder()
        if (preferenceStorage.queueItToken != null) {
            requestUrlBuilder.addQueryParameter(
                HEADER_QUEUE_IT_TOKEN,
                preferenceStorage.queueItToken
            )
        }

        val originRequest = chain.request()
        val request = chain.request().newBuilder()
            .addHeader(HEADER_QUEUE_IT_AJAX_URL, originRequest.url.toString())
            .url(requestUrlBuilder.build())
            .build()

        val response = chain.proceed(request)
        if (mustQueue(response)) {
            cookieStorage.clear()
            preferenceStorage.queueItToken = null
            throw MustBeQueuedException(MUST_QUEUED, response.headers[HEADER_QUEUE_IT_REDIRECT_URL])
        }

        return response
    }

    private fun mustQueue(response: Response) = response.headers.names().contains(
        HEADER_QUEUE_IT_REDIRECT_URL
    )
}
