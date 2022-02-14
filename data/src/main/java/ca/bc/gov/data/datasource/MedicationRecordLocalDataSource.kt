package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.data.local.dao.MedicationRecordDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class MedicationRecordLocalDataSource @Inject constructor(
    private val medicationRecordDao: MedicationRecordDao
) {

    suspend fun insert(medicationRecord: MedicationRecordDto): Long {
        var medicationRecordId =
            medicationRecordDao.insert(medicationRecord = medicationRecord.toEntity())
        if (medicationRecordId == -1L) {
            medicationRecordId =
                medicationRecordDao.getMedicationRecordId(medicationRecord.dispenseDate) ?: -1L
        }
        return medicationRecordId
    }
}
