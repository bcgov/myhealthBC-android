package ca.bc.gov.data

import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.relation.PatientTestResult
import ca.bc.gov.common.model.test.TestResult
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.data.model.mapper.toTestRecord
import ca.bc.gov.data.remote.LaboratoryApi
import ca.bc.gov.data.remote.model.base.Action
import ca.bc.gov.data.remote.model.request.CovidTestRequest
import ca.bc.gov.data.remote.model.request.toMap
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
    suspend fun getCovidTests(request: CovidTestRequest): PatientTestResult {
        val response = safeCall { laboratoryApi.getCovidTests(request.toMap()) }
            ?: throw MyHealthException(SERVER_ERROR, "Invalid Response")

        if (response.error != null) {
            if (Action.MISMATCH.code == response.error.action?.code) {
                throw MyHealthException(SERVER_ERROR_DATA_MISMATCH, response.error.message)
            }
            throw MyHealthException(SERVER_ERROR, response.error.message)
        }

        val patientNameArray = response.payload.covidTestRecords.first().name.split(" ")
        val patient = PatientDto(
            firstName = patientNameArray[0],
            lastName = patientNameArray[1],
            dateOfBirth = request.dateOfBirth.toDate(),
            phn = request.phn
        )

        val testResult = TestResult(
            collectionDate = request.collectionDate.toDate()
        )

        val records = response.payload.covidTestRecords.map { record ->
            record.toTestRecord()
        }
        return PatientTestResult(patient, testResult, records)
    }
}
