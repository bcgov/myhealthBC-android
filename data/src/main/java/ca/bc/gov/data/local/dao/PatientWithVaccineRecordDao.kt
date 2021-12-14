package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
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
    @Query("SELECT * FROM patient")
    fun getPatientsWithVaccineFlow(): Flow<List<PatientWithVaccineRecord>>
}