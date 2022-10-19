package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.request.DependentRegistrationRequest
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

class DependentsRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    suspend fun addDependent(
        hdid: String,
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        phn: String,
        accessToken: String,
    ) {
        val request = DependentRegistrationRequest(
            firstName = firstName,
            lastName = lastName,
            dateOfBirth = dateOfBirth,
            phn = phn
        )
        safeCall {
            healthGatewayPrivateApi.addDependent(hdid, accessToken, request)
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
    }
}
