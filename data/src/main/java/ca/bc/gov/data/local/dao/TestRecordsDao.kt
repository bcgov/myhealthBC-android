package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.bc.gov.data.local.entity.TestRecordEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface TestRecordsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTestRecord(testRecordEntity: TestRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTestRecords(testRecords: List<TestRecordEntity>): List<Long>

    @Query("SELECT * FROM test_record WHERE test_result_id = :testResultId")
    suspend fun getTestRecords(testResultId: Long): List<TestRecordEntity>
}
