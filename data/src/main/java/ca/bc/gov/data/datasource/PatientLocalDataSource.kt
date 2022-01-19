package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.CreatePatientDto
import ca.bc.gov.common.model.patient.Patient
import ca.bc.gov.common.model.patient.PatientWithHealthRecordCount
import ca.bc.gov.data.local.dao.PatientDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientLocalDataSource @Inject constructor(
    private val patientDao: PatientDao
) {

    val patientWithRecordCount: Flow<List<PatientWithHealthRecordCount>> =
        patientDao.getPatientWithRecordCountFlow().map { patientWithRecordCounts ->
            patientWithRecordCounts.map {
                PatientWithHealthRecordCount(
                    it.patientEntity.toDto(),
                    vaccineRecordCount = it.vaccineRecordCount,
                    testResultCount = it.testRecordCount
                )
            }
        }

    /**
     * Inserts [patientDto] records to the database
     * @param patientDto
     * @return if success returns patientId (primary key) else returns -1L
     */
    suspend fun insertPatient(patientDto: CreatePatientDto): Long {
        val patientId = patientDao.insertPatient(patientDto.toEntity())
        if (patientId == -1L) {
            return patientDao.getPatientId(
                patientDto.firstName.uppercase(),
                patientDto.lastName.uppercase(),
                patientDto.dateOfBirth
            ) ?: -1L
        }
        return patientId
    }

    /**
     * Update [patientDao] record in database
     * @param patientDto
     * @return patientId if success else -1L
     */
    suspend fun updatePatient(patientDto: CreatePatientDto): Long {
        return insertPatient(patientDto)
    }

    suspend fun getPatient(patientId: Long): Patient = patientDao.getPatient(patientId).toDto()
}
