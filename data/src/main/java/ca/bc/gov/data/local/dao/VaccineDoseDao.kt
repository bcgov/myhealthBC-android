package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.bc.gov.data.local.entity.VaccineDoseEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface VaccineDoseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVaccineDose(vaccineDoseEntity: VaccineDoseEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllVaccineDose(dose: List<VaccineDoseEntity>): List<Long>

    @Update
    suspend fun updateVaccineDose(vaccineDoseEntity: VaccineDoseEntity): Int

    @Query("SELECT id FROM vaccine_dose where vaccine_record_id = :vaccineRecordId")
    suspend fun getVaccineDoseId(vaccineRecordId: Long): Long?

    @Query("DELETE FROM vaccine_dose WHERE vaccine_record_id = :vaccineRecordId")
    suspend fun deleteVaccineDosesByRecordId(vaccineRecordId: Long): Int
}
