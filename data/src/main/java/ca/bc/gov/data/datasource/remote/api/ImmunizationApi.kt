package ca.bc.gov.data.datasource.remote.api

import ca.bc.gov.data.datasource.remote.model.response.VaccineStatusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Query

/**
 * @author Pinakin Kansara
 */
interface ImmunizationApi {

    @GET("api/immunizationservice/v1/api/PublicVaccineStatus")
    suspend fun getVaccineStatus(@HeaderMap headers: Map<String, String>): Response<VaccineStatusResponse>

    @GET("api/immunizationservice/v1/api/AuthenticatedVaccineStatus")
    suspend fun getVaccineStatus(@Header("Authorization") token: String, @Query("hdid") hdid: String): Response<VaccineStatusResponse>
}
