package ca.bc.gov.repository

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.data.datasource.local.MedicationRecordLocalDataSource
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.datasource.remote.MedicationRemoteDataSource
import ca.bc.gov.data.model.mapper.toListOfMedicationDto
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class MedicationRecordRepository @Inject constructor(
    private val medicationRecordLocalDataSource: MedicationRecordLocalDataSource,
    private val medicationRemoteDataSource: MedicationRemoteDataSource,
    private val encryptedPreferenceStorage: EncryptedPreferenceStorage
) {

    suspend fun insert(medicationRecords: MedicationRecordDto): Long =
        medicationRecordLocalDataSource.insert(medicationRecords)

    suspend fun fetchMedicationStatement(
        token: String,
        hdid: String,
    ): List<MedicationWithSummaryAndPharmacyDto> {
        val protectiveWord: String? = getProtectiveWord()

        return medicationRemoteDataSource.getMedicationStatement(
            token, hdid, protectiveWord
        ).toListOfMedicationDto()
    }

    suspend fun fetchMedicationStatement(
        patientId: Long,
        accessToken: String,
        hdid: String,
        protectiveWord: String?
    ) {
        val medications = medicationRemoteDataSource.getMedicationStatement(
            accessToken, hdid, protectiveWord
        ).toListOfMedicationDto()

        updateMedicationRecords(medications, patientId)
    }

    suspend fun updateMedicationRecords(
        medications: List<MedicationWithSummaryAndPharmacyDto>,
        patientId: Long
    ) {
        medicationRecordLocalDataSource.deletePatientMedicationRecords(patientId)
        medications.forEach { medication ->
            medication.medicationRecord.patientId = patientId

            val medicationRecordId = insert(medication.medicationRecord)
            if (medicationRecordId != -1L) {
                medication.medicationSummary.medicationRecordId = medicationRecordId
                insert(medication.medicationSummary)

                medication.dispensingPharmacy.medicationRecordId = medicationRecordId
                insert(medication.dispensingPharmacy)
            }
        }
    }

    suspend fun insert(medicationSummary: MedicationSummaryDto): Long =
        medicationRecordLocalDataSource.insert(medicationSummary)

    suspend fun insert(dispensingPharmacy: DispensingPharmacyDto): Long =
        medicationRecordLocalDataSource.insert(dispensingPharmacy)

    suspend fun getMedicationWithSummaryAndPharmacy(medicalRecordId: Long): MedicationWithSummaryAndPharmacyDto =
        medicationRecordLocalDataSource.getMedicationWithSummaryAndPharmacy(medicalRecordId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for medicationRecord id=  $medicalRecordId"
            )

    suspend fun isMedicationRecordsAvailableForPatient(patientId: Long): Boolean =
        medicationRecordLocalDataSource.isMedicationRecordsAvailableForPatient(patientId)

    fun getProtectiveWord(): String? {
        return encryptedPreferenceStorage.protectiveWord
    }

    fun getProtectiveWordState(): Int {
        return encryptedPreferenceStorage.protectiveWordState
    }

    fun updateProtectiveWordState(value: Int) {
        encryptedPreferenceStorage.protectiveWordState = value
    }

    fun saveProtectiveWord(word: String) {
        encryptedPreferenceStorage.protectiveWord = word
    }

    suspend fun deleteMedicationData(patientId: Long) {
        medicationRecordLocalDataSource.deletePatientMedicationRecords(patientId)
    }
}
