package ca.bc.gov.data.datasource.remote.interceptor

import ca.bc.gov.common.const.MUST_QUEUED
import ca.bc.gov.common.const.PROTECTIVE_WORD_ERROR_CODE
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.lang.Exception
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
        private const val RESOURCE_PAYLOAD = "resourcePayload"
        private const val LOADED = "loaded"
        private const val RETRY_IN = "retryin"
        private const val RESULT_ERROR = "resultError"
        private const val ACTION_CODE = "actionCode"
        private const val PROTECTED = "PROTECTED"
        private const val BAD_RESPONSE = "Bad response!"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestUrlBuilder = chain.request().url.newBuilder()
        checkQueueItTokenInPref(requestUrlBuilder)

        val originRequest = chain.request()
        val request = originRequest.newBuilder()
            .addHeader(HEADER_QUEUE_IT_AJAX_URL, originRequest.url.toString())
            .url(requestUrlBuilder.build())
            .build()

        var retryCount = 0
        var response: Response?
        var body: ResponseBody? = null
        var stringBody: String? = null
        var loaded = false
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

                if (json.get(RESOURCE_PAYLOAD).isJsonNull) {
                    val resultError = json.getAsJsonObject(RESULT_ERROR) ?: throw IOException(
                        BAD_RESPONSE)
                    if(resultError.get(ACTION_CODE).asString == PROTECTED) {
                        throw ProtectiveWordException(
                            PROTECTIVE_WORD_ERROR_CODE,
                            "Record protected by keyword"
                        )
                    } else {
                        throw IOException(BAD_RESPONSE)
                    }
                }

                var retryInMillis = 0L
                try {
                    val payload =
                        json.getAsJsonObject(RESOURCE_PAYLOAD) ?: throw IOException(BAD_RESPONSE)
                    loaded = payload.get(LOADED).asBoolean
                    retryInMillis = payload.get(RETRY_IN).asLong
                } catch (e: Exception) {
                    loaded = true
                }
                sleepOnRetry(loaded, retryInMillis)

                retryCount++
            }
        } while (!loaded && retryCount < 3)

        if (!loaded || response == null) {
            throw IOException("Getting cached data from health gateway")
        }

        val newBody = stringBody?.toResponseBody(body?.contentType())

        return response.newBuilder()
            .body(newBody).build()
    }

    private fun checkQueueItTokenInPref(requestUrlBuilder: HttpUrl.Builder) {
        if (preferenceStorage.queueItToken != null) {
            requestUrlBuilder.addQueryParameter(
                HEADER_QUEUE_IT_TOKEN,
                preferenceStorage.queueItToken
            )
        }
    }

    private fun sleepOnRetry(loaded: Boolean, retryInMillis: Long) {
        if (!loaded && retryInMillis > 0) {
            Thread.sleep(retryInMillis)
        }
    }

    private fun mustQueue(response: Response) = response.headers.names().contains(
        HEADER_QUEUE_IT_REDIRECT_URL
    )
}
