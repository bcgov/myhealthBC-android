package ca.bc.gov.repository

import ca.bc.gov.common.model.MedicationRecordDto
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
}