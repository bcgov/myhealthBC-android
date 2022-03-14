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
import ca.bc.gov.data.model.mapper.toDispensingPharmacyDto
import ca.bc.gov.data.model.mapper.toMedicationRecordDto
import ca.bc.gov.data.model.mapper.toMedicationSummaryDto
import net.openid.appauth.AuthState
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
        patientId: Long,
        accessToken: String,
        hdid: String,
        protectiveWord: String?
    ) {
        val response = medicationRemoteDataSource.getMedicationStatement(
            patientId, accessToken, hdid, protectiveWord
            accessToken, hdid
        )
        medicationRecordLocalDataSource.deleteAuthenticatedMedicationRecords(patientId)

        response.payload?.forEach { medicationStatementPayload ->
            val medicationRecordId = insert(
                medicationStatementPayload.toMedicationRecordDto(patientId = patientId)
            )
            if (medicationRecordId != -1L) {
                medicationStatementPayload.medicationSummary?.toMedicationSummaryDto(
                    medicationRecordId
                )
                    ?.let { insert(it) }
            }
            if (medicationRecordId != -1L) {
                medicationStatementPayload.dispensingPharmacy?.toDispensingPharmacyDto(
                    medicationRecordId
                )
                    ?.let { insert(it) }
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

    fun getProtectiveWordState() : Int {
        return encryptedPreferenceStorage.protectiveWordState
    }

    fun saveProtectiveWord(word: String) {
        encryptedPreferenceStorage.protectiveWord = word
    }
}
