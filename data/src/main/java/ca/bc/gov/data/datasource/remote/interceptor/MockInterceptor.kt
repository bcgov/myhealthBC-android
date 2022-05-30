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
            uri.endsWith("/MobileConfiguration") ->
                context.assets.readAssetsFile("mock_mobile_configuration_success.json")
            uri.endsWith("/Validate") ->
                context.assets.readAssetsFile("mock_age_restriction_passed.json")
            uri.contains("/UserProfile/") ->
                context.assets.readAssetsFile("mock_patient_profile_success.json")
            uri.contains("/Patient/") ->
                context.assets.readAssetsFile("mock_patient_details_success.json")
            uri.contains("/AuthenticatedVaccineStatus?hdid=") ->
                context.assets.readAssetsFile("mock_authenticated_vaccines_success.json")
            uri.contains("/Laboratory/Covid19Orders?hdid") ->
                context.assets.readAssetsFile("mock_c_19_orders_success.json")
            uri.contains("/MedicationStatement/") ->
                context.assets.readAssetsFile("mock_medication_details_success.json")
            uri.contains("/Laboratory/LaboratoryOrders?hdid=") ->
                context.assets.readAssetsFile("mock_lab_orders_success.json")
            uri.contains("Report?hdid") ->
                context.assets.readAssetsFile("lab_report.json")
            uri.contains("/api/immunizationservice/Immunization?hdid=") ->
                context.assets.readAssetsFile("mock_immunisations_success.json")
            else -> ""
        }

        if (responseString.isEmpty()) {
            return chain.proceed(chain.request())
                .newBuilder()
                .build()
        } else {
            val response = Response.Builder()
                .code(200)
                .protocol(Protocol.HTTP_2)
                .request(chain.request())
                .message(responseString)
                .body(
                    responseString.toByteArray()
                        .toResponseBody("application/json".toMediaTypeOrNull())
                )
                .addHeader("content-type", "application/json")
                .build()
            println(response.request)
            println(response)
            return response
        }
    }

    private fun AssetManager.readAssetsFile(fileName: String): String =
        open(fileName).bufferedReader().use { it.readText() }
}
