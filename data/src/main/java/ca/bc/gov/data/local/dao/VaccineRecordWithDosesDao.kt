package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ca.bc.gov.data.local.entity.relations.VaccineRecordWithDose
import kotlinx.coroutines.flow.Flow

/**
 * @author Pinakin Kansara
 */
@Dao
interface VaccineRecordWithDosesDao {

    @Transaction
    @Query("SELECT * FROM vaccine_record")
    suspend fun getVaccineRecordWithDoses(): List<VaccineRecordWithDose>

    @Transaction
    @Query("SELECT * FROM vaccine_record")
    fun getVaccineRecordWithDosesFlow(): Flow<List<VaccineRecordWithDose>>
}