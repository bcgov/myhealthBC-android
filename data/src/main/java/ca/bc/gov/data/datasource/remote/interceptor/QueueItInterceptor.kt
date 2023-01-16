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
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class QueueItInterceptor @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) : Interceptor {

    companion object {
        private const val MAX_RETRY_COUNT = 5
        private const val STANDARD_RETRY_IN_MILLIS = 5000L
        private const val HEADER_QUEUE_IT_TOKEN = "queueittoken"
        private const val HEADER_QUEUE_IT_AJAX_URL = "x-queueit-ajaxpageurl"
        private const val HEADER_QUEUE_IT_REDIRECT_URL = "x-queueit-redirect"
        private const val RESOURCE_PAYLOAD = "resourcePayload"
        private const val LOADED = "loaded"
        private const val RETRY_IN = "retryin"
        private const val LOAD_STATE = "loadState"
        private const val REFRESH_IN_PROGRESS = "refreshInProgress"
        private const val RESULT_ERROR = "resultError"
        private const val ACTION_CODE = "actionCode"
        private const val PROTECTED = "PROTECTED"
        private const val BAD_RESPONSE = "Bad response!"
    }

    var queueItRequiredUrl: String = ""

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
        var body: ResponseBody?
        var stringBody: String?
        var loaded: Boolean
        do {
            response = chain.proceed(request)
            if (mustQueue(response)) {
                preferenceStorage.queueItToken = null
                val responseHeaders = response.headers
                queueItRequiredUrl = response.request.url.toString()
                throw MustBeQueuedException(
                    MUST_QUEUED,
                    responseHeaders[HEADER_QUEUE_IT_REDIRECT_URL]
                )
            }

            if (response.isSuccessful) {
                body = response.body
                stringBody = body?.string()
                val json = Gson().fromJson(stringBody, JsonObject::class.java)

                if (request.url.toString().contains("MobileConfiguration", true)) {
                    loaded = true
                } else {
                    loaded = checkForLoadedFlag(json)
                    retryCount++
                }
            } else {
                return response
            }
        } while (!loaded && retryCount < MAX_RETRY_COUNT)

        if (response == null) {
            throw IOException("No data from health gateway!")
        }

        val newBody = stringBody?.toResponseBody(body?.contentType())

        return response.newBuilder()
            .body(newBody).build()
    }

    private fun checkForLoadedFlag(json: JsonObject): Boolean {
        var loaded: Boolean
        if (json.get(RESOURCE_PAYLOAD).isJsonNull) {
            checkException(json)
        }
        var retryInMillis = 0L
        try {
            val payload =
                json.getAsJsonObject(RESOURCE_PAYLOAD)
                    ?: throw IOException(BAD_RESPONSE)

            val loadedFlag: Boolean? = payload.get(LOADED)?.asBoolean

            if (loadedFlag == false) {
                loaded = false
                retryInMillis = payload.get(RETRY_IN)?.asLong ?: 0L
            } else { // true or null
                val refreshInProgressResult = handleRefreshInProgress(payload)
                loaded = refreshInProgressResult.loaded
                retryInMillis = refreshInProgressResult.retryInMillis
            }
        } catch (e: Exception) {
            loaded = true
        }
        sleepOnRetry(loaded, retryInMillis)
        return loaded
    }

    /**
     * Check for REFRESH_IN_PROGRESS flag. Behaves like LOADED false state
     */
    private fun handleRefreshInProgress(payload: JsonObject): RefreshInProgress {
        val loaded = try {
            val loadState = payload.getAsJsonObject(LOAD_STATE)
            val refreshInProgress = loadState.get(REFRESH_IN_PROGRESS)?.asBoolean ?: false
            refreshInProgress.not()
        } catch (e: Exception) {
            true
        }

        val retryInMillis = if (loaded) 0L else STANDARD_RETRY_IN_MILLIS
        return RefreshInProgress(loaded, retryInMillis)
    }

    private fun checkQueueItTokenInPref(requestUrlBuilder: HttpUrl.Builder) {
        if (preferenceStorage.queueItToken != null &&
            requestUrlBuilder.toString() == queueItRequiredUrl
        ) {
            requestUrlBuilder.addQueryParameter(
                HEADER_QUEUE_IT_TOKEN,
                preferenceStorage.queueItToken
            )
            queueItRequiredUrl = ""
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

    private fun checkException(json: JsonObject) {
        if (json.get(RESULT_ERROR).isJsonNull) return

        val resultError = json.getAsJsonObject(RESULT_ERROR)
        if (!resultError.get(ACTION_CODE).isJsonNull) {
            if (resultError.get(ACTION_CODE)?.asString == PROTECTED)
                throw ProtectiveWordException(
                    PROTECTIVE_WORD_ERROR_CODE,
                    "Record protected by keyword"
                )
        }
    }
}

private data class RefreshInProgress(
    val loaded: Boolean,
    val retryInMillis: Long
)
