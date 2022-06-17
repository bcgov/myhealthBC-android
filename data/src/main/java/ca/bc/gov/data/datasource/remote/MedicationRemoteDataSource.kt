package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.PROTECTIVE_WORD_ERROR_CODE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.base.Action
import ca.bc.gov.data.datasource.remote.model.response.MedicationStatementResponse
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/*
* Created by amit_metri on 08,February,2022
*/
class MedicationRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    suspend fun getMedicationStatement(
        accessToken: String,
        hdid: String,
        protectiveWord: String?
    ): MedicationStatementResponse {
        val response = safeCall { healthGatewayPrivateApi.getMedicationStatement(hdid, accessToken, protectiveWord) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null && response.error.action != Action.REFRESH) {
            if (response.error.action?.code == Action.PROTECTED.code) {
                throw ProtectiveWordException(PROTECTIVE_WORD_ERROR_CODE, response.error.message)
            }
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }

        // TODO: 08/02/22 Response validations to be placed

        return response
    }
}
