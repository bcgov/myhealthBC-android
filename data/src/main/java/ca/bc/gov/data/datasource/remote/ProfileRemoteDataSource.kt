package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.response.ProfileValidationResponse
import ca.bc.gov.data.datasource.remote.model.response.UserProfileResponse
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/*
* Created by amit_metri on 18,March,2022
*/
class ProfileRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    suspend fun checkAgeLimit(token: String, hdid: String): Boolean {
        val response = safeCall {
            healthGatewayPrivateApi.checkAgeLimit(
                hdid = hdid,
                accessToken = token
            )
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }

        if (!isResponseValid(response)) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }
        return response.resourcePayload!!
    }

    private fun isResponseValid(response: ProfileValidationResponse): Boolean {
        return response.resourcePayload != null
    }

    suspend fun getUserProfile(token: String, hdid: String): UserProfileResponse {
        val response = safeCall { healthGatewayPrivateApi.getUserProfile(hdid, token) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }

        return response
    }
}
