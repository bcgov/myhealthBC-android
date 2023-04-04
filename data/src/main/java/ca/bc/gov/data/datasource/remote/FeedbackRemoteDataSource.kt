package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.request.FeedbackRequest
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

class FeedbackRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {
    suspend fun addFeedback(
        message: String,
        hdid: String,
        accessToken: String
    ) {
        safeCall {
            healthGatewayPrivateApi.addFeedback(
                hdid,
                accessToken,
                FeedbackRequest(message)
            )
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
    }
}
