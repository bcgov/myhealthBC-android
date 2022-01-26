package ca.bc.gov.repository.patient

import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.data.datasource.PatientLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientRepository @Inject constructor(
    private val patientLocalDataSource: PatientLocalDataSource
) {

    suspend fun insertPatient(patientDto: PatientDto): Long =
        patientLocalDataSource.insertPatient(patientDto)

    suspend fun updatePatient(patientDto: PatientDto): Long =
        patientLocalDataSource.updatePatient(patientDto)

    suspend fun getPatient(patientId: Long): PatientDto =
        patientLocalDataSource.getPatient(patientId)
}
