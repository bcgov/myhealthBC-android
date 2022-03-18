package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.const.SERVER_ERROR_INCORRECT_PHN
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.relation.PatientWithTestResultsAndRecordsDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.common.model.test.TestResultDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPublicApi
import ca.bc.gov.data.datasource.remote.model.base.Action
import ca.bc.gov.data.datasource.remote.model.request.CovidTestRequest
import ca.bc.gov.data.datasource.remote.model.request.toMap
import ca.bc.gov.data.datasource.remote.model.response.LabTestPdfResponse
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toTestRecord
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class LaboratoryRemoteDataSource @Inject constructor(
    private val healthGatewayPublicApi: HealthGatewayPublicApi,
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    /**
     *  Covid test result is not providing phn number of dob in response
     *  so we have to add phn and dob in order to record data in DB properly.
     *  This can be done on repository layer or data source layer.
     */
    suspend fun getCovidTests(request: CovidTestRequest): PatientWithTestResultsAndRecordsDto {
        val response = safeCall { healthGatewayPublicApi.getCovidTests(request.toMap()) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            if (Action.MISMATCH.code == response.error.action?.code) {
                throw MyHealthException(SERVER_ERROR_DATA_MISMATCH, response.error.message)
            }
            if ("Error parsing phn" == response.error.message) {
                throw MyHealthException(SERVER_ERROR_INCORRECT_PHN, response.error.message)
            }
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }

        val patient = PatientDto(
            fullName = response.payload.covidTestRecords.first().name,
            dateOfBirth = request.dateOfBirth.toDate(),
            phn = request.phn
        )

        val testResult = TestResultDto(
            collectionDate = request.collectionDate.toDate()
        )

        val records = response.payload.covidTestRecords.map { record ->
            record.toTestRecord()
        }

        val testResultWithTesRecord = TestResultWithRecordsDto(
            testResult,
            records
        )
        return PatientWithTestResultsAndRecordsDto(patient, listOfNotNull(testResultWithTesRecord))
    }

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

        if (response.error != null) {
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
