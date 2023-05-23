package ca.bc.gov.repository.services

import ca.bc.gov.data.datasource.remote.PatientServicesRemoteDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientServicesRepository @Inject constructor(
    private val patientServicesRemoteDataSource: PatientServicesRemoteDataSource
) {
    suspend fun fetchPatientData(hdId: String) = patientServicesRemoteDataSource.getPatientData(hdId)

    suspend fun fetchPatientDataFile(hdid: String, fileId: String) =
        patientServicesRemoteDataSource.getPatientDataFile(hdid, fileId)
}
