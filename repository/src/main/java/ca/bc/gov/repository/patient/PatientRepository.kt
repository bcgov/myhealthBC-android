package ca.bc.gov.repository.patient

import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.patient.PatientListDto
import ca.bc.gov.data.datasource.PatientLocalDataSource
import kotlinx.coroutines.flow.Flow
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

    suspend fun getPatientList(): Flow<PatientListDto> =
        patientLocalDataSource.getPatientList()

    suspend fun insertAuthenticatedPatient(patientDto: PatientDto): Long =
        patientLocalDataSource.insertAuthenticatedPatient(patientDto)
}
