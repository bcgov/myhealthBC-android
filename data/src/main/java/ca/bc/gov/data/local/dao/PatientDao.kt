package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.PatientOrderUpdate
import ca.bc.gov.data.local.entity.relations.PatientWithHealthRecordCount
import ca.bc.gov.data.local.entity.relations.PatientWithMedicationRecords
import ca.bc.gov.data.local.entity.relations.PatientWithTestResultsAndRecords
import ca.bc.gov.data.local.entity.relations.PatientWithVaccineAndDoses
import ca.bc.gov.data.local.entity.relations.TestResultWithRecordsAndPatient
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(patientEntity: PatientEntity): Long

    @Update(entity = PatientEntity::class)
    suspend fun updatePatientsOrder(patientOrderUpdates: List<PatientOrderUpdate>)

    @Query("SELECT id FROM patient WHERE full_name = :fullName AND dob = :dateOdBirth")
    suspend fun getPatientId(fullName: String, dateOdBirth: Instant): Long?

    @Query("SELECT * FROM patient WHERE dob = :dateOdBirth")
    suspend fun getPatientByDob(dateOdBirth: Instant): List<PatientEntity>?

    @Query("SELECT * FROM patient where id = :patientId")
    suspend fun getPatient(patientId: Long): PatientEntity

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithVaccineAndDoses(patientId: Long): PatientWithVaccineAndDoses?

    @Transaction
    @Query("SELECT * FROM patient WHERE full_name = :fullName AND dob = :dateOfBirth ")
    suspend fun getPatientWithVaccineAndDoses(
        fullName: String,
        dateOfBirth: Instant
    ): List<PatientWithVaccineAndDoses>

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithTestResultsAndRecords(patientId: Long): PatientWithTestResultsAndRecords?

    @Transaction
    @Query("SELECT * FROM test_result WHERE id = :testResultId")
    suspend fun getPatientWithTestResultAndRecords(testResultId: Long): TestResultWithRecordsAndPatient?

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithMedicationRecords(patientId: Long): PatientWithMedicationRecords?

    @Transaction
    @Query(
        """
        SELECT P.*,
        COUNT(DISTINCT V.id) AS vaccineRecordCount,
        COUNT(DISTINCT T.id) as testRecordCount,
        COUNT (DISTINCT M.id) as medicationRecordCount FROM patient P
        LEFT JOIN vaccine_record V on V.patient_id = P.id
        LEFT JOIN test_result T on T.patient_id = P.id
        LEFT JOIN medication_record M on M.patient_id = P.id
        GROUP BY P.id ORDER BY P.authentication_status
    """
    )
    fun getPatientWithHealthRecordCountFlow(): Flow<List<PatientWithHealthRecordCount>>

    @Transaction
    @Query(
        "SELECT" +
            " (SELECT COUNT(*) FROM test_result WHERE data_source = 'BCSC') +" +
            " (SELECT COUNT(*) FROM vaccine_record WHERE data_source = 'BCSC') +" +
            " (SELECT COUNT(*) FROM medication_record WHERE data_source = 'BCSC') as SumCount"
    )
    suspend fun getBcscSourceHealthRecordCount(): Int

    @Transaction
    @Query("SELECT * FROM patient ORDER BY patient_order ASC")
    fun getPatientWithVaccineAndDosesFlow(): Flow<List<PatientWithVaccineAndDoses>>

    @Transaction
    @Query("SELECT * FROM patient")
    fun getPatientWithTestResultsAndRecordsFlow(): Flow<List<PatientWithTestResultsAndRecords>>

    @Query("SELECT * FROM patient")
    fun getPatientList(): Flow<List<PatientEntity>>

    @Query("DELETE FROM patient WHERE id = :patientId")
    suspend fun deletePatientById(patientId: Long): Int

    @Transaction
    @Query(
        "DELETE FROM patient WHERE id = " +
            "(SELECT id FROM patient WHERE authentication_status = 'AUTHENTICATED')"
    )
    suspend fun deleteAuthenticatedPatient()
}
