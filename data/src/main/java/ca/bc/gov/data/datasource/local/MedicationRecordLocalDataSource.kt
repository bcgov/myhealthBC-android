package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.DispensingPharmacyDto
import ca.bc.gov.common.model.MedicationRecordDto
import ca.bc.gov.common.model.MedicationSummaryDto
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.data.datasource.local.dao.DispensingPharmacyDao
import ca.bc.gov.data.datasource.local.dao.MedicationRecordDao
import ca.bc.gov.data.datasource.local.dao.MedicationSummaryDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class MedicationRecordLocalDataSource @Inject constructor(
    private val medicationRecordDao: MedicationRecordDao,
    private val medicationSummaryDao: MedicationSummaryDao,
    private val dispensingPharmacyDao: DispensingPharmacyDao
) {

    suspend fun insert(medicationRecord: MedicationRecordDto): Long {
        var medicationRecordId = -1L
        medicationRecordId = medicationRecordDao.insert(medicationRecord.toEntity())
        return medicationRecordId
    }

    suspend fun insert(medicationSummary: MedicationSummaryDto): Long {
        return medicationSummaryDao.insert(medicationSummary.toEntity())
    }

    suspend fun insert(dispensingPharmacy: DispensingPharmacyDto): Long {
        return dispensingPharmacyDao.insert(dispensingPharmacy.toEntity())
    }

    suspend fun getMedicationWithSummaryAndPharmacy(medicationRecordId: Long): MedicationWithSummaryAndPharmacyDto? =
        medicationRecordDao.getMedicationWithSummaryAndPharmacy(medicationRecordId)?.toDto()

    suspend fun deleteAuthenticatedMedicationRecords(patientId: Long): Int =
        medicationRecordDao.deleteAuthenticatedMedicationRecords(patientId)

    suspend fun isMedicationRecordsAvailableForPatient(patientId: Long): Boolean =
        medicationRecordDao.getCountOfMedicationRecords(patientId) > 0
    suspend fun deletePatientMedicationRecords(patientId: Long): Int =
        medicationRecordDao.deletePatientMedicationRecords(patientId)
}
