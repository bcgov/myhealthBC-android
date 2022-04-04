package ca.bc.gov.repository

import ca.bc.gov.data.datasource.remote.LaboratoryRemoteDataSource
import ca.bc.gov.data.datasource.remote.model.request.CovidTestRequest
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class FetchTestResultRepository @Inject constructor(
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    private val laboratoryRemoteDataSource: LaboratoryRemoteDataSource
) {

    suspend fun fetchCovidTestRecord(
        phn: String,
        dateOfBirth: String,
        collectionDate: String
    ): Pair<Long, Long> {
        val response = laboratoryRemoteDataSource.getCovidTests(
            CovidTestRequest(
                phn, dateOfBirth, collectionDate
            )
        )
        return patientWithTestResultRepository.insertTestResult(response)
    }
}
