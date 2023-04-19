package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.base.patient.PatientPayload
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

class PatientRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    suspend fun getPatient(token: String, hdid: String): PatientPayload {
        val response = safeCall { healthGatewayPrivateApi.getPatient(token = token, hdid = hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        return response.payload ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
    }
}
