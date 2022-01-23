package ca.bc.gov.data.remote.interceptor

import ca.bc.gov.common.const.MUST_QUEUED
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.data.local.preference.EncryptedPreferenceStorage
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class QueueItInterceptor @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) : Interceptor {

    companion object {
        private const val TAG = "QueueItInterceptor"
        private const val HEADER_QUEUE_IT_TOKEN = "queueittoken"
        private const val HEADER_QUEUE_IT_AJAX_URL = "x-queueit-ajaxpageurl"
        private const val HEADER_QUEUE_IT_REDIRECT_URL = "x-queueit-redirect"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestUrlBuilder = chain.request().url.newBuilder()
        if (preferenceStorage.queueItToken != null) {
            requestUrlBuilder.addQueryParameter(
                HEADER_QUEUE_IT_TOKEN,
                preferenceStorage.queueItToken
            )
        }

        val originRequest = chain.request()
        val request = originRequest.newBuilder()
            .addHeader(HEADER_QUEUE_IT_AJAX_URL, originRequest.url.toString())
            .url(requestUrlBuilder.build())
            .build()

        var retryCount = 0
        var response: Response?
        var body: ResponseBody? = null
        var stringBody: String? = null
        do {
            response = chain.proceed(request)
            if (mustQueue(response)) {
                preferenceStorage.queueItToken = null
                val responseHeaders = response.headers
                throw MustBeQueuedException(
                    MUST_QUEUED,
                    responseHeaders[HEADER_QUEUE_IT_REDIRECT_URL]
                )
            }

            if (response.isSuccessful) {
                body = response.body
                stringBody = body?.string()
                val json = Gson().fromJson(stringBody, JsonObject::class.java)
                val payload = json.getAsJsonObject("resourcePayload")
                val loaded = payload.get("loaded").asBoolean
                val retryInMillis = payload.get("retryin").asLong
                if (!loaded && retryCount <2) {
                    response = null
                    Thread.sleep(retryInMillis)
                    retryCount++
                }else{
                    throw IOException("Exception")
                }
            }
        } while (response == null && retryCount <3)

        if (response == null) {
            response = chain.proceed(request)
        }

        val newBody = stringBody?.toResponseBody(body?.contentType())

        return response.newBuilder()
            .body(newBody).build()
    }

    private fun mustQueue(response: Response) = response.headers.names().contains(
        HEADER_QUEUE_IT_REDIRECT_URL
    )
}
