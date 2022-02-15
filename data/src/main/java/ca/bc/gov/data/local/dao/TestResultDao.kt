package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.bc.gov.data.local.entity.TestRecordEntity
import ca.bc.gov.data.local.entity.TestResultEntity
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Dao
interface TestResultDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(testResultEntity: TestResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(testRecordEntity: TestRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(testRecords: List<TestRecordEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTestRecords(testRecords: List<TestRecordEntity>): List<Long>

    @Query("DELETE FROM test_result WHERE id = :testResultId")
    suspend fun delete(testResultId: Long): Int

    @Query("SELECT id FROM test_result WHERE patient_id = :patientId AND collection_date = :collectionDate")
    suspend fun getTestResultId(patientId: Long, collectionDate: Instant): Long?
}
