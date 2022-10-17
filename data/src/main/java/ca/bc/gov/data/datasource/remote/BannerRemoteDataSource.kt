package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.banner.BannerDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPublicApi
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

class BannerRemoteDataSource @Inject constructor(
    private val publicApi: HealthGatewayPublicApi
) {

    suspend fun fetchCommunicationBanner(): BannerDto? {
        val response = safeCall { publicApi.getCommunicationBanner() } ?: throw MyHealthException(
            SERVER_ERROR,
            MESSAGE_INVALID_RESPONSE
        )

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }

        return response.payload?.toDto()
    }
}
