package ca.bc.gov.data.datasource.remote.api

import ca.bc.gov.data.datasource.remote.model.response.BannerResponse
import ca.bc.gov.data.datasource.remote.model.response.CovidTestResponse
import ca.bc.gov.data.datasource.remote.model.response.VaccineStatusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap

/**
 * @author Pinakin Kansara
 */
interface HealthGatewayPublicApi {

    companion object {
        private const val BASE_COMMUNICATION_BANNER = "api/gatewayapiservice/Communication"
        private const val BASE_IMMUNIZATION_SERVICE = "api/immunizationservice"
        private const val BASE_LABORATORY_SERVICE = "api/laboratoryservice"
    }

    @GET("$BASE_COMMUNICATION_BANNER/Mobile")
    suspend fun getCommunicationBanner(): Response<BannerResponse>

    @GET("$BASE_IMMUNIZATION_SERVICE/PublicVaccineStatus")
    suspend fun getVaccineStatus(@HeaderMap headers: Map<String, String>): Response<VaccineStatusResponse>

    @GET("$BASE_LABORATORY_SERVICE/PublicLaboratory/CovidTests")
    suspend fun getCovidTests(@HeaderMap headers: Map<String, String>): Response<CovidTestResponse>
}
