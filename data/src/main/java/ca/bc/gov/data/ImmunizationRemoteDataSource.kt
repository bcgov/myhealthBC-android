package ca.bc.gov.data

import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.const.SERVER_ERROR_INCORRECT_PHN
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import ca.bc.gov.data.model.VaccineStatus
import ca.bc.gov.data.model.mapper.toVaccineStatus
import ca.bc.gov.data.remote.ImmunizationApi
import ca.bc.gov.data.remote.model.base.Action
import ca.bc.gov.data.remote.model.request.VaccineStatusRequest
import ca.bc.gov.data.remote.model.request.toMap
import ca.bc.gov.data.remote.model.response.VaccineStatusResponse
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
            ?: throw MyHealthNetworkException(SERVER_ERROR, "Invalid response")

        return validate(response)
    }

    suspend fun getVaccineStatus(token: String, hdid: String): VaccineStatus {
        val response = safeCall { immunizationApi.getVaccineStatus(token, hdid) }
            ?: throw MyHealthNetworkException(SERVER_ERROR, "Invalid response")

        return validate(response)
    }

    private fun validate(response: VaccineStatusResponse): VaccineStatus {
        if (response.error != null) {
            if (response.error.action == null) {
                throw MyHealthNetworkException(SERVER_ERROR, response.error.message)
            }
            if (Action.MISMATCH.code == response.error.action.code) {
                throw MyHealthNetworkException(SERVER_ERROR_DATA_MISMATCH, response.error.message)
            }
            if ("Error parsing phn" == response.error.message) {
                throw MyHealthNetworkException(SERVER_ERROR_INCORRECT_PHN, response.error.message)
            }
            throw MyHealthNetworkException(SERVER_ERROR, response.error.message)
        }

        if (response.payload == null) {
            throw MyHealthNetworkException(SERVER_ERROR, "Invalid Response")
        }
        return response.payload.toVaccineStatus()
    }
}
