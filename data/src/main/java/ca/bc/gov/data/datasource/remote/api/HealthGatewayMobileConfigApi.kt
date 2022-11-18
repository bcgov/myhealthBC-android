package ca.bc.gov.data.datasource.remote.api

import ca.bc.gov.data.datasource.remote.model.response.MobileConfigurationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author: Created by Rashmi Bambhania on 23,May,2022
 */
interface HealthGatewayMobileConfigApi {

    companion object {
        private const val MOBILE_CONFIGURATION = "MobileConfiguration"
    }

    @GET("$MOBILE_CONFIGURATION")
    suspend fun getMobileConfiguration(
        @Query("api-version") apiVersion: String? = null
    ): Response<MobileConfigurationResponse>
}
