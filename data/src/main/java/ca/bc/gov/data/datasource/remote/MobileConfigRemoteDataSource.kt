package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPublicApi
import ca.bc.gov.data.datasource.remote.model.response.MobileConfigurationResponse
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 16,May,2022
 */
class MobileConfigRemoteDataSource @Inject constructor(private val healthGatewayPublicApi: HealthGatewayPublicApi) {

    suspend fun getBaseUrl(): MobileConfigurationResponse {
        return safeCall { healthGatewayPublicApi.getBaseUrl() }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
    }
}
