package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 20,June,2022
 */
class HealthVisitsRemoteDataSource @Inject constructor(private val healthGatewayPrivateApi: HealthGatewayPrivateApi) {

    suspend fun getHealthVisits(token: String, hdid: String): List<HealthVisitsDto> {
        val response = safeCall { healthGatewayPrivateApi.getHealthVisits(token, hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }

        return response.toDto()
    }
}
