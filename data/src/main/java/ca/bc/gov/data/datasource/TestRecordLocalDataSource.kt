package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.data.local.dao.TestRecordsDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TestRecordLocalDataSource @Inject constructor(
    private val testRecordsDao: TestRecordsDao
) {

    suspend fun insertAllTestRecords(id: Long, recordDtos: List<TestRecordDto>) {
        val records = testRecordsDao.getTestRecords(id)
        if (records.isEmpty()) {
            testRecordsDao.insertTestRecords(recordDtos.map { it.toEntity() })
        }
    }

    suspend fun insertAllAuthenticatedTestRecords(id: Long, recordDtos: List<TestRecordDto>): List<Long> {
        return testRecordsDao.insertTestRecords(recordDtos.map { it.toEntity() })
    }

    suspend fun getTestRecords(testResultId: Long): List<TestRecordDto> =
        testRecordsDao.getTestRecords(testResultId).map { it.toDto() }
}
