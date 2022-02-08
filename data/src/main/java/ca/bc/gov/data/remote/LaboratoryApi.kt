package ca.bc.gov.data.remote

import ca.bc.gov.data.remote.model.response.AuthenticatedCovidTestResponse
import ca.bc.gov.data.remote.model.response.CovidTestResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Query

/**
 * @author Pinakin Kansara
 */
interface LaboratoryApi {

    @GET("api/laboratoryservice/v1/api/PublicLaboratory/CovidTests")
    suspend fun getCovidTests(@HeaderMap headers: Map<String, String>): Response<CovidTestResponse>

    //can create interceptor
    @GET("api/laboratoryservice/v1/api/Laboratory/Covid19Orders")
    suspend fun getAuthenticatedCovidTests(
        @Header("Authorization") token: String,
        @Query("hdid") hdid: String
    ): Response<AuthenticatedCovidTestResponse>
}
