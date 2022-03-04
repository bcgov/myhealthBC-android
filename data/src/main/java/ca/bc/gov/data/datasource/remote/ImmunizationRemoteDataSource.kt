package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.const.SERVER_ERROR_INCORRECT_PHN
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.data.datasource.remote.api.ImmunizationApi
import ca.bc.gov.data.datasource.remote.model.base.Action
import ca.bc.gov.data.datasource.remote.model.request.VaccineStatusRequest
import ca.bc.gov.data.datasource.remote.model.request.toMap
import ca.bc.gov.data.datasource.remote.model.response.VaccineStatusResponse
import ca.bc.gov.data.model.VaccineStatus
import ca.bc.gov.data.model.mapper.toVaccineStatus
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class ImmunizationRemoteDataSource @Inject constructor(
    private val immunizationApi: ImmunizationApi
) {

    suspend fun getVaccineStatus(request: VaccineStatusRequest): VaccineStatus {
        val response = safeCall { immunizationApi.getVaccineStatus(request.toMap()) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        return validate(response)
    }

    suspend fun getVaccineStatus(token: String, hdid: String): VaccineStatus {
        val response = safeCall { immunizationApi.getVaccineStatus(token, hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        return validate(response)
    }

    private fun validate(response: VaccineStatusResponse): VaccineStatus {
        if (response.error != null) {
            if (response.error.action == null) {
                throw MyHealthException(SERVER_ERROR, response.error.message)
            }
            if (Action.MISMATCH.code == response.error.action.code) {
                throw MyHealthException(SERVER_ERROR_DATA_MISMATCH, response.error.message)
            }
            if ("Error parsing phn" == response.error.message) {
                throw MyHealthException(SERVER_ERROR_INCORRECT_PHN, response.error.message)
            }
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }

        if (response.payload == null) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }
        return response.payload.toVaccineStatus()
    }
}
