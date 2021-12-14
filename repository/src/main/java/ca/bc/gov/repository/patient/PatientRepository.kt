package ca.bc.gov.repository.patient

import ca.bc.gov.common.model.CreatePatientDto
import ca.bc.gov.common.model.patient.Patient
import ca.bc.gov.data.datasource.PatientLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientRepository @Inject constructor(
    private val patientLocalDataSource: PatientLocalDataSource
) {

    suspend fun insertPatient(patientDto: CreatePatientDto): Long =
        patientLocalDataSource.insertPatient(patientDto)

    suspend fun updatePatient(patientDto: CreatePatientDto): Long =
        patientLocalDataSource.updatePatient(patientDto)

    suspend fun getPatient(patientId: Long): Patient = patientLocalDataSource.getPatient(patientId)
}