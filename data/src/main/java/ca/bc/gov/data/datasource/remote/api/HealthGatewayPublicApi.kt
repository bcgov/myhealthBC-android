package ca.bc.gov.data.datasource.remote.api

import ca.bc.gov.data.datasource.remote.model.response.CovidTestResponse
import ca.bc.gov.data.datasource.remote.model.response.MobileConfigurationResponse
import ca.bc.gov.data.datasource.remote.model.response.VaccineStatusResponse
import ca.bc.gov.data.datasource.remote.model.response.VerifyLoadResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

/**
 * @author Pinakin Kansara
 */
interface HealthGatewayPublicApi {

    companion object {
        private const val BASE_IMMUNIZATION_SERVICE = "api/immunizationservice"
        private const val BASE_LABORATORY_SERVICE = "api/laboratoryservice"
        private const val MOBILE_CONFIGURATION = "MobileConfiguration"
    }

    @GET("$BASE_IMMUNIZATION_SERVICE/PublicVaccineStatus")
    suspend fun getVaccineStatus(@HeaderMap headers: Map<String, String>): Response<VaccineStatusResponse>

    @GET("$BASE_LABORATORY_SERVICE/PublicLaboratory/CovidTests")
    suspend fun getCovidTests(@HeaderMap headers: Map<String, String>): Response<CovidTestResponse>

    @GET("$MOBILE_CONFIGURATION")
    suspend fun verifyLoad(): Response<VerifyLoadResponse>

    @GET("$MOBILE_CONFIGURATION")
    suspend fun getBaseUrl(
        @Query("api-version") apiVersion: String? = null
    ): Response<MobileConfigurationResponse>
}
