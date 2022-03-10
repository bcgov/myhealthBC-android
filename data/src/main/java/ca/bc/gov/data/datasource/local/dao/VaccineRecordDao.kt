package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineDoseEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineRecordEntity

/**
 * @author Pinakin Kandara
 */
@Dao
interface VaccineRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vaccineRecordEntity: VaccineRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vaccineDoseEntity: VaccineDoseEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dose: List<VaccineDoseEntity>): List<Long>

    @Update
    suspend fun update(vaccineRecordEntity: VaccineRecordEntity): Int

    @Update
    suspend fun update(vaccineDoseEntity: VaccineDoseEntity): Int

    @Query("DELETE FROM vaccine_record WHERE id = :vaccineRecordId")
    suspend fun delete(vaccineRecordId: Long): Int

    @Query("DELETE FROM vaccine_dose WHERE vaccine_record_id = :vaccineRecordId")
    suspend fun deleteVaccineDosesByRecordId(vaccineRecordId: Long): Int

    @Query("SELECT id FROM vaccine_dose where vaccine_record_id = :vaccineRecordId")
    suspend fun getVaccineDoseId(vaccineRecordId: Long): Long?

    @Query("SELECT id from vaccine_record where patient_id = :patientId")
    suspend fun getVaccineRecordId(patientId: Long): Long?

    @Query("SELECT * FROM vaccine_dose WHERE vaccine_record_id = :vaccineRecordId")
    suspend fun getVaccineDoses(vaccineRecordId: Long): List<VaccineDoseEntity>

    @Query("DELETE FROM vaccine_record WHERE patient_id = :patientId")
    suspend fun deletePatientVaccineRecords(patientId: Long): Int
}
