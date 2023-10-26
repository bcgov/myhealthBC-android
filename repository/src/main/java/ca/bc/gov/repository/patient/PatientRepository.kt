package ca.bc.gov.repository.patient

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.patient.PatientWithCovidOrderAndTestDto
import ca.bc.gov.common.model.patient.PatientWithDataDto
import ca.bc.gov.common.model.patient.PatientWithHealthVisitsDto
import ca.bc.gov.common.model.patient.PatientWithImmunizationRecordAndForecastDto
import ca.bc.gov.common.model.patient.PatientWithLabOrderAndLatTestsDto
import ca.bc.gov.common.model.patient.PatientWithSpecialAuthorityDto
import ca.bc.gov.common.model.relation.PatientWithMedicationRecordDto
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
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

    suspend fun getPatientWithMedicationRecords(patientId: Long): PatientWithMedicationRecordDto =
        patientLocalDataSource.getPatientWithMedicationRecords(patientId)
            ?: throw getNoRecordFoundException(patientId)

    suspend fun getPatientWithLabOrdersAndLabTests(patientId: Long): PatientWithLabOrderAndLatTestsDto =
        patientLocalDataSource.getPatientWithLabOrdersAndLabTests(patientId)
            ?: throw getNoRecordFoundException(patientId)

    suspend fun getPatientWithCovidOrdersAndCovidTests(patientId: Long): PatientWithCovidOrderAndTestDto =
        patientLocalDataSource.getPatientWithCovidOrderAndCovidTests(patientId)
            ?: throw getNoRecordFoundException(patientId)

    suspend fun getPatientWithImmunizationRecordAndForecast(patientId: Long): PatientWithImmunizationRecordAndForecastDto =
        patientLocalDataSource.getPatientWithImmunizationRecordAndForecast(patientId)
            ?: throw getNoRecordFoundException(patientId)

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

    suspend fun getPatientWithHealthVisits(patientId: Long): PatientWithHealthVisitsDto =
        patientLocalDataSource.getPatientWithHealthVisits(patientId)
            ?: throw getNoRecordFoundException(patientId)

    suspend fun getPatientWithSpecialAuthority(patientId: Long): PatientWithSpecialAuthorityDto =
        patientLocalDataSource.getPatientWithSpecialAuthority(patientId)
            ?: throw getNoRecordFoundException(patientId)

    suspend fun getPatientWithHospitalVisits(patientId: Long): List<HospitalVisitDto> {
        return patientLocalDataSource.getPatientWithHospitalVisits(patientId)?.hospitalVisits
            ?: throw getNoRecordFoundException(patientId)
    }

    suspend fun getPatientWithClinicalDocuments(patientId: Long): List<ClinicalDocumentDto> =
        patientLocalDataSource.getPatientWithClinicalDocuments(patientId)?.clinicalDocuments
            ?: throw getNoRecordFoundException(patientId)

    suspend fun getPatientWithData(patientId: Long): PatientWithDataDto =
        patientLocalDataSource.getPatientWithData(patientId)
            ?: throw getNoRecordFoundException(patientId)

    suspend fun getPatientWithImmunizationRecommendations(patientId: Long) =
        patientLocalDataSource.getPatientWithImmunizationRecommendations(patientId)
            ?: throw getNoRecordFoundException(patientId)

    suspend fun getPatientWithDependents(patientId: Long) =
        patientLocalDataSource.getPatientWithDependents(patientId)
            ?: throw getNoRecordFoundException(patientId)

    private fun getNoRecordFoundException(patientId: Long) = MyHealthException(
        DATABASE_ERROR, "No record found for patient id=  $patientId"
    )
}
