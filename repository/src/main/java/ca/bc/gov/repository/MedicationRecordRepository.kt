package ca.bc.gov.repository

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.data.datasource.MedicationRecordLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class MedicationRecordRepository @Inject constructor(
    private val medicationRecordLocalDataSource: MedicationRecordLocalDataSource
) {

    suspend fun insert(medicationRecord: MedicationRecordDto): Long =
        medicationRecordLocalDataSource.insert(medicationRecord)

    suspend fun insert(medicationSummary: MedicationSummaryDto): Long =
        medicationRecordLocalDataSource.insert(medicationSummary)

    suspend fun insert(dispensingPharmacy: DispensingPharmacyDto): Long =
        medicationRecordLocalDataSource.insert(dispensingPharmacy)

    suspend fun getMedicationWithSummaryAndPharmacy(medicalRecordId: Long): MedicationWithSummaryAndPharmacyDto =
        medicationRecordLocalDataSource.getMedicationWithSummaryAndPharmacy(medicalRecordId)
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for medicationRecord id=  $medicalRecordId"
            )
}