package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ca.bc.gov.data.local.entity.TestResultEntity
import ca.bc.gov.data.local.entity.relations.TestResultWithRecord
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Dao
interface TestResultDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTestResult(testResultEntity: TestResultEntity): Long

    @Query("SELECT id FROM test_result WHERE patient_id = :patientId AND collection_date = :collectionDate")
    suspend fun getTestResultId(patientId: Long, collectionDate: Instant): Long?

    @Query("SELECT * FROM test_result WHERE patient_id = :patientId ORDER BY collection_date DESC")
    suspend fun getTestResults(patientId: Long): List<TestResultEntity>

    @Transaction
    @Query("SELECT * from test_result WHERE id = :testResultId")
    suspend fun getTestResultWithRecord(testResultId: Long): TestResultWithRecord

    @Query("DELETE FROM test_result WHERE id = :testResultId")
    suspend fun delete(testResultId: Long): Int
}
