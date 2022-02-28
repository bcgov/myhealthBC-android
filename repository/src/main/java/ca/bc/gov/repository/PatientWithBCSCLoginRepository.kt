package ca.bc.gov.repository

import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.utils.toDateTime
import ca.bc.gov.data.datasource.remote.PatientRemoteDataSource
import javax.inject.Inject

class PatientWithBCSCLoginRepository @Inject constructor(
    private val patientRemoteDataSource: PatientRemoteDataSource
) {

    suspend fun getPatient(token: String, hdid: String): PatientDto {
        val patient = patientRemoteDataSource.getPatient(token, hdid)

        val fullNameBuilder = StringBuilder()
        if (patient.firstName != null) {
            fullNameBuilder.append(patient.firstName)
            fullNameBuilder.append(" ")
        }
        if (patient.lastName != null) {
            fullNameBuilder.append(patient.lastName)
        }
        return PatientDto(
            fullName = fullNameBuilder.toString(),
            dateOfBirth = patient.birthDate!!.toDateTime(),
            phn = patient.personalHealthNumber,
            authenticationStatus = AuthenticationStatus.AUTHENTICATED
        )
    }
}
