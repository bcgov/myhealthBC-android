package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.InvalidResponseException
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

class ClinicalDocumentRemoteDataSource @Inject constructor(
    private val privateApi: HealthGatewayPrivateApi
) {

    suspend fun fetchPdf(
        token: String,
        hdid: String,
        fileId: String
    ): String {
        val response = safeCall {
            privateApi.getClinicalDocumentPdf(accessToken = token, hdid = hdid, fileId = fileId)
        }

        val data = response?.resourcePayload?.data ?: throw InvalidResponseException()

        if (response.error != null) throw InvalidResponseException()

        return data
    }

    suspend fun getClinicalDocument(token: String, hdid: String): List<ClinicalDocumentDto> {
        val response = safeCall { privateApi.getClinicalDocument(token, hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }

        return response.toDto()
    }
}
