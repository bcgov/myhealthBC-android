package ca.bc.gov.repository

import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.utils.toDateTime
import ca.bc.gov.data.datasource.remote.PatientRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import javax.inject.Inject

class PatientWithBCSCLoginRepository @Inject constructor(
    private val patientRemoteDataSource: PatientRemoteDataSource
) {

    suspend fun getPatient(token: String, hdid: String): PatientDto {
        val patientResponse = patientRemoteDataSource.getPatient(token, hdid)

        val fullNameBuilder = StringBuilder()
        if (patientResponse.firstName != null) {
            fullNameBuilder.append(patientResponse.firstName)
            fullNameBuilder.append(" ")
        }
        if (patientResponse.lastName != null) {
            fullNameBuilder.append(patientResponse.lastName)
        }
        return PatientDto(
            fullName = fullNameBuilder.toString(),
            dateOfBirth = patientResponse.birthDate!!.toDateTime(),
            phn = patientResponse.personalHealthNumber,
            authenticationStatus = AuthenticationStatus.AUTHENTICATED,
            firstName = patientResponse.firstName.orEmpty(),
            lastName = patientResponse.lastName.orEmpty(),
            physicalAddress = patientResponse.physicalAddress?.toDto(),
            mailingAddress = patientResponse.mailingAddress?.toDto(),
        )
    }
}
