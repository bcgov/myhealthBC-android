package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.test.TestRecord
import ca.bc.gov.data.local.dao.TestRecordsDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TestRecordLocalDataSource @Inject constructor(
    private val testRecordsDao: TestRecordsDao
) {

    suspend fun insertAllTestRecords(records: List<TestRecord>): List<Long> {
        return testRecordsDao.insertTestRecords(records.map { it.toEntity() })
    }
}
