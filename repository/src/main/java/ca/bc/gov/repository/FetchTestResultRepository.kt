package ca.bc.gov.repository

import ca.bc.gov.data.datasource.remote.LaboratoryRemoteDataSource
import ca.bc.gov.data.remote.model.request.CovidTestRequest
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class FetchTestResultRepository @Inject constructor(
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    private val laboratoryRemoteDataSource: LaboratoryRemoteDataSource
) {

    suspend fun fetchCovidTestRecord(phn: String, dateOfBirth: String, collectionDate: String): Long {
        val response = laboratoryRemoteDataSource.getCovidTests(
            CovidTestRequest(
                phn, dateOfBirth, collectionDate
            )
        )
        return patientWithTestResultRepository.insertTestResult(response)
    }

    suspend fun fetchCovidTestRecord(patientId: Long, token: String, hdid: String) {
        val response = laboratoryRemoteDataSource.getCovidTests(token, hdid)
        patientWithTestResultRepository.deleteAuthenticatedTestRecords(patientId)
        response.forEach {
            patientWithTestResultRepository.insertAuthenticatedTestResult(
                patientId,
                it
            )
        }
    }

    suspend fun fetchLabTestRecord(token: String, hdid: String) {
        val response = laboratoryRemoteDataSource.getLabTests(token, hdid)
    }
}
