package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.model.base.Action
import ca.bc.gov.data.datasource.remote.model.response.LabTestPdfResponse
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class LaboratoryRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    suspend fun getCovidTests(
        token: String,
        hdid: String
    ): List<CovidOrderWithCovidTestDto> {
        val response = safeCall { healthGatewayPrivateApi.getCovidTests(token, hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }
        return response.toDto()
    }

    suspend fun getLabTests(
        token: String,
        hdid: String
    ): List<LabOrderWithLabTestDto> {

        val response = safeCall { healthGatewayPrivateApi.getLabTests(token, hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null && response.error.action != Action.REFRESH) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }

        return response.toDto()
    }

    suspend fun getLabTestInPdf(
        token: String,
        hdid: String,
        reportId: String,
        isCovid19: Boolean
    ): LabTestPdfResponse {

        val response = safeCall {
            healthGatewayPrivateApi.getLabTestReportPdf(
                token,
                reportId,
                hdid,
                isCovid19
            )
        }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }

        if (!isLabTestPdfResponseValid(response)) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }

        return response
    }

    private fun isLabTestPdfResponseValid(response: LabTestPdfResponse): Boolean {
        var isValid = false
        if (response.resourcePayload != null)
            with(response.resourcePayload) {
                isValid = when {
                    mediaType.isNullOrBlank() ||
                        encoding.isNullOrBlank() ||
                        data.isNullOrBlank() -> {
                        false
                    }
                    mediaType != "application/pdf" ||
                        encoding != "base64" -> {
                        false
                    }
                    else -> {
                        true
                    }
                }
            }
        return isValid
    }
}
