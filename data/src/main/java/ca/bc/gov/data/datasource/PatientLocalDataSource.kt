package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.patient.PatientWithHealthRecordCount
import ca.bc.gov.common.model.relation.PatientWithTestResultsAndRecordsDto
import ca.bc.gov.common.utils.toUniquePatientName
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsAndPatientDto
import ca.bc.gov.data.local.dao.PatientDao
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.PatientOrderUpdate
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

    val patientWithVaccineAndDoses: Flow<List<PatientWithVaccineAndDosesDto>> =
        patientDao.getPatientWithVaccineAndDosesFlow().map { patientWithVaccineAndDoses ->
            patientWithVaccineAndDoses.map { patient ->
                patient.toDto()
            }
        }

    val patientWithRecordCount: Flow<List<PatientWithHealthRecordCount>> =
        patientDao.getPatientWithHealthRecordCountFlow().map { patientWithRecordCounts ->
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
    suspend fun insertPatient(patientDto: PatientDto): Long {
        val patientList = patientDao.getPatientByDob(patientDto.dateOfBirth)
        if (patientList.isNullOrEmpty()) {
            return patientDao.insertPatient(patientDto.toEntity())
        } else {
            for (i in patientList.indices) {
                if (patientList[i].fullName.toUniquePatientName()
                        .equals(patientDto.fullName.toUniquePatientName(), true)
                ) {
                    return patientList[i].id
                }
            }
            return patientDao.insertPatient(patientDto.toEntity())
        }
    }

    suspend fun insertAuthenticatedPatient(patientDto: PatientDto): Long {
        val patientList = patientDao.getPatientByDob(patientDto.dateOfBirth)
        return if (patientList.isNullOrEmpty()) {
            patientDao.insertPatient(patientDto.toEntity())
        } else {
            for (i in patientList.indices) {
                if (patientList[i].fullName.toUniquePatientName()
                        .equals(patientDto.fullName.toUniquePatientName(), true)
                ) {
                    patientDao.deletePatientById(patientList[i].id)
                }
            }
            patientDao.insertPatient(patientDto.toEntity())
        }
    }

    /**
     * Update [patientDao] record in database
     * @param patientDto
     * @return patientId if success else -1L
     */
    suspend fun update(patientDto: PatientDto): Long {
        return insertPatient(patientDto)
    }

    suspend fun updatePatientsOrder(patientOrderUpdates: List<PatientOrderUpdate>) =
        patientDao.updatePatientsOrder(patientOrderUpdates)

    suspend fun getPatient(patientId: Long): PatientDto = patientDao.getPatient(patientId).toDto()

    suspend fun getPatientWithVaccineAndDoses(patientId: Long): PatientWithVaccineAndDosesDto? =
        patientDao.getPatientWithVaccineAndDoses(patientId)?.toDto()

    suspend fun getPatientWithVaccineAndDoses(patient: PatientEntity): List<PatientWithVaccineAndDosesDto> =
        patientDao.getPatientWithVaccineAndDoses(patient.fullName, patient.dateOfBirth)
            .map { it.toDto() }

    suspend fun getPatientWithTestResultsAndRecords(patientId: Long): PatientWithTestResultsAndRecordsDto? =
        patientDao.getPatientWithTestResultsAndRecords(patientId)?.toDto()

    suspend fun getPatientWithTestResultAndRecords(testResultId: Long): TestResultWithRecordsAndPatientDto? =
        patientDao.getPatientWithTestResultAndRecords(testResultId)?.toDto()
}
