package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 24,June,2022
 */
class SpecialAuthorityRemoteDataSource @Inject constructor(private val healthGatewayPrivateApi: HealthGatewayPrivateApi) {
    suspend fun getSpecialAuthority(token: String, hdid: String): List<SpecialAuthorityDto> {
        val response = safeCall { healthGatewayPrivateApi.getSpecialAuthority(token, hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }

        return response.toDto()
    }
}
