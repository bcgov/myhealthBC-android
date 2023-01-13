package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

class HospitalVisitRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    suspend fun getHospitalVisit(token: String, hdid: String): List<HospitalVisitDto> {
        val response = safeCall { healthGatewayPrivateApi.getHospitalVisit(token, hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }

        return response.payload?.list?.map {
            it.toDto()
        } ?: emptyList()
    }
}
