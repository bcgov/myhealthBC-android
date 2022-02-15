package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.relations.PatientWithHealthRecordCount
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPatient(patientEntity: PatientEntity): Long

    @Query("SELECT id FROM patient WHERE full_name = :fullName AND dob = :dateOdBirth")
    suspend fun getPatientId(fullName: String, dateOdBirth: Instant): Long?

    @Query("SELECT * FROM patient WHERE dob = :dateOdBirth")
    suspend fun getPatientByDob(dateOdBirth: Instant): List<PatientEntity>?

    @Query("SELECT * FROM patient where id = :patientId")
    suspend fun getPatient(patientId: Long): PatientEntity

    @Transaction
    @Query(
        """
        SELECT P.*,
        COUNT(DISTINCT V.id) AS vaccineRecordCount, COUNT(DISTINCT T.id) as testRecordCount FROM patient P
        LEFT JOIN vaccine_record V on V.patient_id = P.id
        LEFT JOIN test_result T on T.patient_id = P.id
        GROUP BY P.id
    """
    )
    fun getPatientWithRecordCountFlow(): Flow<List<PatientWithHealthRecordCount>>

    @Query("SELECT * FROM patient")
    fun getPatientList(): Flow<List<PatientEntity>>

    @Query("DELETE FROM patient WHERE id = :patientId")
    suspend fun deletePatientById(patientId: Long): Int
}
