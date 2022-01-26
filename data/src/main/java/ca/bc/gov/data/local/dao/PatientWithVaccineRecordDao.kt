package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.PatientOrderUpdate
import ca.bc.gov.data.local.entity.VaccineDoseEntity
import ca.bc.gov.data.local.entity.relations.PatientWithVaccineRecord
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Dao
interface PatientWithVaccineRecordDao {

    @Transaction
    @Query("SELECT * FROM patient WHERE first_name = :firstName AND last_name = :lastName AND dob = :dateOfBirth ")
    suspend fun getPatientsWithVaccine(
        firstName: String,
        lastName: String,
        dateOfBirth: Instant
    ): List<PatientWithVaccineRecord>

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithVaccine(patientId: Long): PatientWithVaccineRecord?

    @Transaction
    @Query("SELECT * FROM patient ORDER BY patient_order ASC")
    fun getPatientsWithVaccineFlow(): Flow<List<PatientWithVaccineRecord>>

    @Query("SELECT * FROM vaccine_dose WHERE vaccine_record_id = :vaccineRecordId")
    suspend fun getVaccineDoses(vaccineRecordId: Long): List<VaccineDoseEntity>

    @Update(entity = PatientEntity::class)
    suspend fun updatePatientOrder(patientOrderUpdates: List<PatientOrderUpdate>)
}
