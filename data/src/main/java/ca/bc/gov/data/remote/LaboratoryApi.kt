package ca.bc.gov.data.remote

import ca.bc.gov.data.remote.model.response.CovidTestResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap

/**
 * @author Pinakin Kansara
 */
interface LaboratoryApi {

    @GET("api/laboratoryservice/v1/api/PublicLaboratory/CovidTests")
    suspend fun getCovidTests(@HeaderMap headers: Map<String, String>): Response<CovidTestResponse>
}
