package ca.bc.gov.repository

import ca.bc.gov.common.model.PatientWithBCSCLoginDto
import ca.bc.gov.data.PatientRemoteDataSource
import javax.inject.Inject

class PatientWithBCSCLoginRepository @Inject constructor(
    private val patientRemoteDataSource: PatientRemoteDataSource
) {

    suspend fun getPatient(token: String, hdid: String): PatientWithBCSCLoginDto {
        val patient = patientRemoteDataSource.getPatient(token, hdid)
        return PatientWithBCSCLoginDto(
            birthDate = patient.birthDate,
            firstName = patient.firstName,
            lastName = patient.lastName,
            gender = patient.gender,
            hdid = patient.hdid,
            personalHealthNumber = patient.personalHealthNumber
        )
    }
}