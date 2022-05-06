package ca.bc.gov.repository.patient

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.patient.PatientWithCovidOrderAndTestDto
import ca.bc.gov.common.model.patient.PatientWithLabOrderAndLatTestsDto
import ca.bc.gov.common.model.relation.PatientWithMedicationRecordDto
import ca.bc.gov.common.model.relation.PatientWithTestResultsAndRecordsDto
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsAndPatientDto
import ca.bc.gov.data.datasource.local.PatientLocalDataSource
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.PatientOrderUpdate
import ca.bc.gov.repository.QrCodeGeneratorRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientRepository @Inject constructor(
    private val patientLocalDataSource: PatientLocalDataSource,
    private val qrCodeGeneratorRepository: QrCodeGeneratorRepository
) {

    val patientWithVaccineAndDoses =
        patientLocalDataSource.patientWithVaccineAndDoses.map { patientWithVaccineAndDoses ->
            patientWithVaccineAndDoses.filter { record ->
                record.vaccineWithDoses != null
            }.map { record ->

                record.vaccineWithDoses?.let { vaccineWithDosesDto ->
                    vaccineWithDosesDto.vaccine
                        .qrCodeImage =
                        qrCodeGeneratorRepository.generateQRCode(vaccineWithDosesDto.vaccine.shcUri)
                }
                record
            }
        }

    suspend fun getBcscDataRecordCount(): Int {
        return patientLocalDataSource.getBcscSourceHealthRecordCount()
    }

    suspend fun insert(patientDto: PatientDto): Long =
        patientLocalDataSource.insert(patientDto)

    suspend fun update(patientDto: PatientDto): Long =
        patientLocalDataSource.update(patientDto)

    suspend fun updatePatientsOrder(patientOrderMapping: List<Pair<Long, Long>>) {
        patientLocalDataSource.updatePatientsOrder(
            patientOrderMapping.map {
                PatientOrderUpdate(it.first, it.second)
            }
        )
    }

    suspend fun getPatientWithVaccineAndDoses(patientId: Long): PatientWithVaccineAndDosesDto =
        patientLocalDataSource.getPatientWithVaccineAndDoses(patientId) ?: throw MyHealthException(
            DATABASE_ERROR, "No record found for patient id=  $patientId"
        )

    suspend fun getPatientWithVaccineAndDoses(patient: PatientEntity): List<PatientWithVaccineAndDosesDto> =
        patientLocalDataSource.getPatientWithVaccineAndDoses(patient)

    suspend fun getPatientWithTestResultsAndRecords(patientId: Long): PatientWithTestResultsAndRecordsDto =
        patientLocalDataSource.getPatientWithTestResultsAndRecords(patientId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for patient id=  $patientId"
            )

    suspend fun getPatientWithTestResultAndRecords(testResultId: Long): TestResultWithRecordsAndPatientDto =
        patientLocalDataSource.getPatientWithTestResultAndRecords(testResultId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for testResult id=  $testResultId"
            )

    suspend fun getPatientWithMedicationRecords(patientId: Long): PatientWithMedicationRecordDto =
        patientLocalDataSource.getPatientWithMedicationRecords(patientId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for patient id=  $patientId"
            )

    suspend fun getPatientWithLabOrdersAndLabTests(patientId: Long): PatientWithLabOrderAndLatTestsDto =
        patientLocalDataSource.getPatientWithLabOrdersAndLabTests(patientId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for patient id=  $patientId"
            )

    suspend fun getPatientWithCovidOrdersAndCovidTests(patientId: Long): PatientWithCovidOrderAndTestDto =
        patientLocalDataSource.getPatientWithCovidOrderAndCovidTests(patientId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for patient id=  $patientId"
            )

    suspend fun insertAuthenticatedPatient(patientDto: PatientDto): Long =
        patientLocalDataSource.insertAuthenticatedPatient(patientDto)

    suspend fun findPatientByAuthStatus(authenticationStatus: AuthenticationStatus): PatientDto =
        patientLocalDataSource.findPatientByAuthStatus(authenticationStatus)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for patient status=  $authenticationStatus"
            )

    suspend fun deleteByPatientId(patientId: Long) {
        patientLocalDataSource.deleteByPatientId(patientId)
    }
}
