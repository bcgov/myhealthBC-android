package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.bc.gov.data.local.entity.VaccineRecordEntity

/**
 * @author Pinakin Kandara
 */
@Dao
interface VaccineRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVaccineRecord(vaccineRecordEntity: VaccineRecordEntity): Long

    @Query("SELECT id from vaccine_record where patient_id = :patientId")
    suspend fun getVaccineRecordId(patientId: Long): Long?

    @Update
    suspend fun updateVaccineRecord(vaccineRecordEntity: VaccineRecordEntity): Int

    @Query("SELECT * FROM vaccine_record WHERE patient_id = :patientId")
    suspend fun getVaccineRecords(patientId: Long): List<VaccineRecordEntity>

    @Query("DELETE FROM vaccine_record WHERE id = :vaccineRecordId")
    suspend fun delete(vaccineRecordId: Long): Int
}