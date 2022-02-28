package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.const.SERVER_ERROR_INCORRECT_PHN
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.relation.PatientWithTestResultsAndRecordsDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto
import ca.bc.gov.common.utils.formatInPattern
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.data.model.mapper.toTestRecord
import ca.bc.gov.data.remote.LaboratoryApi
import ca.bc.gov.data.remote.model.base.Action
import ca.bc.gov.data.remote.model.request.CovidTestRequest
import ca.bc.gov.data.remote.model.request.toMap
import ca.bc.gov.data.remote.model.response.LabTestResponse
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class LaboratoryRemoteDataSource @Inject constructor(
    private val laboratoryApi: LaboratoryApi
) {

    /**
     * TODO:- Covid test result is not providing phn number of dob in response
     *  so we have to add phn and dob in order to record data in DB properly.
     *  This can be done on repository layer or data source layer.
     */
    suspend fun getCovidTests(request: CovidTestRequest): PatientWithTestResultsAndRecordsDto {
        val response = safeCall { laboratoryApi.getCovidTests(request.toMap()) }
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
    ): List<TestResultWithRecordsDto> {
        val response = safeCall { laboratoryApi.getCovidTests(token, hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }

        val list = arrayListOf<TestResultWithRecordsDto>()
        for (i in response.payload.orders.indices) {
            if (!response.payload.orders[i].labResults.isNullOrEmpty() &&
                response.payload.orders[i].labResults!![0].collectedDateTime != null
            ) {
                val testResult = TestResultDto(
                    collectionDate = response.payload.orders[i].labResults!![0].collectedDateTime!!.formatInPattern()
                        .toDate()
                )
                var records = emptyList<TestRecordDto>()
                for (j in response.payload.orders[i].labResults!!.indices) {
                    records = response.payload.orders[i].labResults!!.map { labResult ->
                        labResult.toTestRecord()
                    }
                }
                records.map { record ->
                    record.labName = response.payload.orders[i].reportingLab ?: ""
                }
                list.add(TestResultWithRecordsDto(testResult, records))
            }
        }

        return list
    }

    suspend fun getLabTests(
        token: String,
        hdid: String
    ): LabTestResponse {

        val response = safeCall { laboratoryApi.getLabTests(token, hdid) }
            ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

        if (response.error != null) {
            throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
        }

        return response
    }
}
