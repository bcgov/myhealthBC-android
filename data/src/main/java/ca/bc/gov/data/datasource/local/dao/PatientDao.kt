package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.PatientOrderUpdate
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithClinicalDocuments
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithCovidOrderAndCovidTest
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithHealthVisits
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithHospitalVisits
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithImmunizationRecordAndForecast
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithLabOrdersAndLabTests
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithMedicationRecords
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithSpecialAuthorities
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithVaccineAndDoses
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

    @Query("SELECT * FROM patient WHERE dob = :dateOdBirth")
    suspend fun getPatientByDob(dateOdBirth: Instant): List<PatientEntity>?

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
    suspend fun getPatientWithMedicationRecords(patientId: Long): PatientWithMedicationRecords?

    @Transaction
    @Query("SELECT * FROM patient ORDER BY patient_order ASC")
    fun getPatientWithVaccineAndDosesFlow(): Flow<List<PatientWithVaccineAndDoses>>

    @Query("DELETE FROM patient WHERE id = :patientId")
    suspend fun deletePatientById(patientId: Long): Int

    @Transaction
    @Query("DELETE FROM patient WHERE id = (SELECT id FROM patient WHERE authentication_status = 'AUTHENTICATED')")
    suspend fun deleteAuthenticatedPatient()

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithLabOrderAndTests(patientId: Long): PatientWithLabOrdersAndLabTests?

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithCovidOrderAndCovidTests(patientId: Long): PatientWithCovidOrderAndCovidTest?

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithImmunizationRecordAndForecast(patientId: Long): PatientWithImmunizationRecordAndForecast

    @Query("SELECT * FROM patient WHERE authentication_status = :authenticationStatus")
    suspend fun findPatientByAuthStatus(authenticationStatus: AuthenticationStatus): PatientEntity?

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithHealthVisits(patientId: Long): PatientWithHealthVisits?

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithHospitalVisits(patientId: Long): PatientWithHospitalVisits?

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithClinicalDocuments(patientId: Long): PatientWithClinicalDocuments?

    @Transaction
    @Query("SELECT * FROM patient WHERE id = :patientId")
    suspend fun getPatientWithSpecialAuthority(patientId: Long): PatientWithSpecialAuthorities?

    @Query("SELECT id FROM patient WHERE authentication_status = 'AUTHENTICATED'")
    suspend fun getAuthenticatedPatientId(): Long?

    @Query("DELETE FROM patient WHERE authentication_status = 'DEPENDENT'")
    suspend fun deleteDependentPatients(): Int
}
