package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.BuildConfig
import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

class ClinicalDocumentRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    suspend fun getClinicalDocument(token: String, hdid: String): List<ClinicalDocumentDto> {
        if (BuildConfig.FLAG_CLINICAL_DOCUMENTS.not()) return emptyList()

        val response = safeCall { healthGatewayPrivateApi.getClinicalDocument(token, hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }

        return response.toDto()
    }
}
