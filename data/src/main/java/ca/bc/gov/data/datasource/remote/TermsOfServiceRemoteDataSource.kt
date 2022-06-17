package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.response.TermsOfServiceResponse
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TermsOfServiceRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    suspend fun getTermsOfService(): TermsOfServiceResponse {
        val response = safeCall { healthGatewayPrivateApi.getTermsOfService() }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }

        return response
    }
}
