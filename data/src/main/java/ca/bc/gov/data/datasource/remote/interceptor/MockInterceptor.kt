package ca.bc.gov.data.datasource.remote.interceptor

import android.content.Context
import android.content.res.AssetManager
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/*
* Created by amit_metri on 09,March,2022
*/
class MockInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val uri = chain.request().url.toUri().toString()
        val responseString = when {
            uri.contains("https://dev.healthgateway.gov.bc.ca/api/laboratoryservice/v1/api/Laboratory/LaboratoryOrders") ->
                context.assets.readAssetsFile("lab_orders.json")
            uri.contains("Report?hdid") ->
                context.assets.readAssetsFile("lab_report.json")
            else -> ""
        }

        if (responseString.isEmpty()) {
            return chain.proceed(chain.request())
                .newBuilder()
                .build()
        } else {
            return chain.proceed(chain.request())
                .newBuilder()
                .code(200)
                .protocol(Protocol.HTTP_2)
                .message(responseString)
                .body(
                    responseString.toByteArray()
                        .toResponseBody("application/json".toMediaTypeOrNull())
                )
                .addHeader("content-type", "application/json")
                .build()
        }
    }

    private fun AssetManager.readAssetsFile(fileName: String): String = open(fileName).bufferedReader().use { it.readText() }
}
