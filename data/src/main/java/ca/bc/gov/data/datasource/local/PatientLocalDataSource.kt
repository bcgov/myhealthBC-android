package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.patient.PatientWithCovidOrderAndTestDto
import ca.bc.gov.common.model.patient.PatientWithHealthRecordCount
import ca.bc.gov.common.model.patient.PatientWithImmunizationRecordAndForecastDto
import ca.bc.gov.common.model.patient.PatientWithLabOrderAndLatTestsDto
import ca.bc.gov.common.model.relation.PatientWithMedicationRecordDto
import ca.bc.gov.common.model.relation.PatientWithTestResultsAndRecordsDto
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsAndPatientDto
import ca.bc.gov.common.utils.toUniquePatientName
import ca.bc.gov.data.datasource.local.dao.PatientDao
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.PatientOrderUpdate
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
                    testResultCount = it.testRecordCount,
                    labTestCount = it.labTestCount,
                    covidTestCount = it.covidTestCount,
                    immunizationCount = it.immunizationCount,
                    medicationRecordCount = it.medicationRecordCount
                )
            }
        }

    suspend fun getBcscSourceHealthRecordCount(): Int {
        return patientDao.getBcscSourceHealthRecordCount()
    }

    /**
     * Inserts [patient] records to the database
     * @param patient
     * @return if success returns patientId (primary key) else returns -1L
     */
    suspend fun insert(patient: PatientDto): Long {
        val patientList = patientDao.getPatientByDob(patient.dateOfBirth)
        if (patientList.isNullOrEmpty()) {
            return patientDao.insert(patient.toEntity())
        } else {
            for (i in patientList.indices) {
                if (patientList[i].fullName.toUniquePatientName()
                    .equals(patient.fullName.toUniquePatientName(), true)
                ) {
                    return patientList[i].id
                }
            }
            return patientDao.insert(patient.toEntity())
        }
    }

    val patients = patientDao.getPatientList().map { patient -> patient.map { it.toDto() } }

    suspend fun insertAuthenticatedPatient(patientDto: PatientDto): Long {
        patientDao.deleteAuthenticatedPatient()
        val patientList = patientDao.getPatientByDob(patientDto.dateOfBirth)
        return if (patientList.isNullOrEmpty()) {
            patientDao.insert(patientDto.toEntity())
        } else {
            for (i in patientList.indices) {
                if (patientList[i].fullName.toUniquePatientName()
                    .equals(patientDto.fullName.toUniquePatientName(), true)
                ) {
                    patientDao.deletePatientById(patientList[i].id)
                }
            }
            patientDao.insert(patientDto.toEntity())
        }
    }

    /**
     * Update [patientDao] record in database
     * @param patient
     * @return patientId if success else -1L
     */
    suspend fun update(patient: PatientDto): Long {
        return insert(patient)
    }

    suspend fun updatePatientsOrder(patientOrderUpdates: List<PatientOrderUpdate>) =
        patientDao.updatePatientsOrder(patientOrderUpdates)

    suspend fun getPatientWithVaccineAndDoses(patientId: Long): PatientWithVaccineAndDosesDto? =
        patientDao.getPatientWithVaccineAndDoses(patientId)?.toDto()

    suspend fun getPatientWithVaccineAndDoses(patient: PatientEntity): List<PatientWithVaccineAndDosesDto> =
        patientDao.getPatientWithVaccineAndDoses(patient.fullName, patient.dateOfBirth)
            .map { it.toDto() }

    suspend fun getPatientWithTestResultsAndRecords(patientId: Long): PatientWithTestResultsAndRecordsDto? =
        patientDao.getPatientWithTestResultsAndRecords(patientId)?.toDto()

    suspend fun getPatientWithTestResultAndRecords(testResultId: Long): TestResultWithRecordsAndPatientDto? =
        patientDao.getPatientWithTestResultAndRecords(testResultId)?.toDto()

    suspend fun getPatientWithMedicationRecords(patientId: Long): PatientWithMedicationRecordDto? =
        patientDao.getPatientWithMedicationRecords(patientId)?.toDto()

    suspend fun getPatientWithLabOrdersAndLabTests(patientId: Long): PatientWithLabOrderAndLatTestsDto? =
        patientDao.getPatientWithLabOrderAndTests(patientId)?.toDto()

    suspend fun getPatientWithCovidOrderAndCovidTests(patientId: Long): PatientWithCovidOrderAndTestDto? =
        patientDao.getPatientWithCovidOrderAndCovidTests(patientId)?.toDto()

    suspend fun getPatientWithImmunizationRecordAndForecast(patientId: Long): PatientWithImmunizationRecordAndForecastDto? =
        patientDao.getPatientWithImmunizationRecordAndForecast(patientId)?.toDto()

    suspend fun deleteBcscAuthenticatedPatientData() {
        patientDao.deleteAuthenticatedPatient()
    }

    suspend fun isAuthenticatedPatient(patientId: Long): Boolean =
        patientDao.isAuthenticatedPatient(patientId) > 0

    suspend fun findPatientByAuthStatus(authenticationStatus: AuthenticationStatus): PatientDto? =
        patientDao.findPatientByAuthStatus(authenticationStatus)?.toDto()
}
