package ca.bc.gov.data.remote

import ca.bc.gov.data.remote.model.response.VaccineStatusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap

/**
 * @author Pinakin Kansara
 */
interface ImmunizationApi {

    @GET("api/immunizationservice/v1/api/PublicVaccineStatus")
    suspend fun getVaccineStatus(@HeaderMap headers: Map<String, String>): Response<VaccineStatusResponse>
}
