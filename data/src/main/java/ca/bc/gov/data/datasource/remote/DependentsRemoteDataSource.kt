package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.const.SERVER_ERROR_INCORRECT_PHN
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.base.Action
import ca.bc.gov.data.datasource.remote.model.base.dependent.DependentInformation
import ca.bc.gov.data.datasource.remote.model.base.dependent.DependentPayload
import ca.bc.gov.data.datasource.remote.model.request.DependentRegistrationRequest
import ca.bc.gov.data.datasource.remote.model.response.DependentResponse
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

class DependentsRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    suspend fun fetchAllDependents(hdid: String, accessToken: String) =
        safeCall {
            healthGatewayPrivateApi.fetchAllDependents(hdid, accessToken)
        }?.payload ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

    suspend fun deleteDependent(
        guardianHdid: String,
        dependentHdid: String,
        accessToken: String,
        dependentDto: DependentDto
    ) = safeCall {
        val dependentPayload = DependentPayload(
            DependentInformation(
                hdid = dependentDto.hdid,
                firstname = dependentDto.firstname,
                lastname = dependentDto.lastname,
                phn = dependentDto.phn,
                dateOfBirth = dependentDto.dateOfBirth.toString(),
                gender = dependentDto.gender,
            ),
            ownerId = dependentDto.ownerId,
            delegateId = dependentDto.delegateId,
            reasonCode = dependentDto.reasonCode,
            totalDelegateCount = dependentDto.totalDelegateCount,
            version = dependentDto.version,
        )

        healthGatewayPrivateApi.deleteDependent(
            guardianHdid = guardianHdid,
            dependentHdid = dependentHdid,
            accessToken = accessToken,
            dependentPayload = dependentPayload
        )
    }

    suspend fun addDependent(
        hdid: String,
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        phn: String,
        accessToken: String,
    ): DependentPayload {
        val request = DependentRegistrationRequest(
            firstName = firstName,
            lastName = lastName,
            dateOfBirth = dateOfBirth,
            phn = phn
        )
        val response = safeCall {
            healthGatewayPrivateApi.addDependent(hdid, accessToken, request)
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        return validate(response)
    }

    private fun validate(response: DependentResponse): DependentPayload {
        if (response.error != null && response.error.action != Action.REFRESH) {
            if (Action.MISMATCH.code == response.error.action?.code) {
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

        return response.payload
    }
}
