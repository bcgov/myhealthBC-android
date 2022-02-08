package ca.bc.gov.repository

import ca.bc.gov.common.model.AuthenticatedCovidTestDto
import ca.bc.gov.data.LaboratoryRemoteDataSource
import ca.bc.gov.data.remote.model.base.AuthenticatedCovidTestPayload
import ca.bc.gov.data.remote.model.request.CovidTestRequest
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class FetchTestResultRepository @Inject constructor(
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    private val laboratoryRemoteDataSource: LaboratoryRemoteDataSource
) {

    suspend fun fetchTestRecord(phn: String, dateOfBirth: String, collectionDate: String): Long {
        val response = laboratoryRemoteDataSource.getCovidTests(
            CovidTestRequest(
                phn, dateOfBirth, collectionDate
            )
        )
        return patientWithTestResultRepository.insertTestResult(response)
    }

    suspend fun fetchAuthenticatedTestRecord(token: String, hdid: String): AuthenticatedCovidTestDto {
        return laboratoryRemoteDataSource.getAuthenticatedCovidTests(token, hdid)
    }
}
