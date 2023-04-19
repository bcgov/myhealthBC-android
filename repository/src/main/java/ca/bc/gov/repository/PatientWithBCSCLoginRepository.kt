package ca.bc.gov.repository

import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.data.datasource.remote.PatientRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import javax.inject.Inject

class PatientWithBCSCLoginRepository @Inject constructor(
    private val patientRemoteDataSource: PatientRemoteDataSource
) {

    suspend fun getPatient(token: String, hdid: String): PatientDto {
        val patientResponse = patientRemoteDataSource.getPatient(token, hdid)
        return patientResponse.toDto()
    }
}
