package ca.bc.gov.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ca.bc.gov.data.local.entity.TestRecordEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface TestRecordsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestRecord(testRecordEntity: TestRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestRecords(testRecords: List<TestRecordEntity>): List<Long>
}