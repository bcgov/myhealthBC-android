package ca.bc.gov.data.remote

import ca.bc.gov.data.remote.model.response.AuthenticatedCovidTestResponse
import ca.bc.gov.data.remote.model.response.CovidTestResponse
import ca.bc.gov.data.remote.model.response.LabTestResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Query

/**
 * @author Pinakin Kansara
 */
interface LaboratoryApi {

    companion object {
        private const val HDID = "hdid"
        private const val AUTHORIZATION = "Authorization"
        private const val BASE_URL = "api/laboratoryservice/v1/api"
    }

    @GET("$BASE_URL/PublicLaboratory/CovidTests")
    suspend fun getCovidTests(@HeaderMap headers: Map<String, String>): Response<CovidTestResponse>

    @GET("$BASE_URL/Laboratory/Covid19Orders")
    suspend fun getCovidTests(
        @Header(AUTHORIZATION) token: String,
        @Query(HDID) hdid: String
    ): Response<AuthenticatedCovidTestResponse>

    @GET("$BASE_URL/Laboratory/LaboratoryOrders")
    suspend fun getLabTests(
        @Header(AUTHORIZATION) token: String,
        @Query(HDID) hdid: String
    ): Response<LabTestResponse>
}
