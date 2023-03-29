package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ca.bc.gov.data.datasource.local.entity.medication.MedicationRecordEntity
import ca.bc.gov.data.datasource.local.entity.relations.MedicationWithSummaryAndPharmacy

/**
 * @author Pinakin Kansara
 */
@Dao
interface MedicationRecordDao : BaseDao<MedicationRecordEntity> {
    @Transaction
    @Query("SELECT * FROM medication_record WHERE id = :medicationRecordId")
    suspend fun getMedicationWithSummaryAndPharmacy(medicationRecordId: Long): MedicationWithSummaryAndPharmacy?

    @Query("SELECT COUNT(*) FROM medication_record WHERE patient_id = :patientId ")
    suspend fun getCountOfMedicationRecords(patientId: Long): Long

    @Query("DELETE FROM medication_record WHERE patient_id = :patientId")
    suspend fun deletePatientMedicationRecords(patientId: Long): Int
}
