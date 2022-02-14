package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.bc.gov.data.local.entity.MedicationRecordEntity
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Dao
interface MedicationRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(medicationRecord: MedicationRecordEntity): Long

    @Query("SELECT id from medication_record where dispense_date = :dispenseDate")
    suspend fun getMedicationRecordId(dispenseDate: Instant): Long?
}