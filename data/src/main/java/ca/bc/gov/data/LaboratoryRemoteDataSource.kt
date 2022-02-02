package ca.bc.gov.data

import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.const.SERVER_ERROR_INCORRECT_PHN
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.relation.PatientTestResultDto
import ca.bc.gov.common.model.test.TestResultDto
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
    suspend fun getCovidTests(request: CovidTestRequest): PatientTestResultDto {
        val response = safeCall { laboratoryApi.getCovidTests(request.toMap()) }
            ?: throw MyHealthNetworkException(SERVER_ERROR, "Invalid Response")

        if (response.error != null) {
            if (Action.MISMATCH.code == response.error.action?.code) {
                throw MyHealthNetworkException(SERVER_ERROR_DATA_MISMATCH, response.error.message)
            }
            if ("Error parsing phn" == response.error.message) {
                throw MyHealthNetworkException(SERVER_ERROR_INCORRECT_PHN, response.error.message)
            }
            throw MyHealthNetworkException(SERVER_ERROR, response.error.message)
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
        return PatientTestResultDto(patient, testResult, records)
    }
}
