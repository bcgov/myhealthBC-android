package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto
import ca.bc.gov.data.datasource.local.dao.TestResultDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TestResultLocalDataSource @Inject constructor(
    private val testResultDao: TestResultDao
) {

    /**
     * @param testResult
     * @return return resultId or -1L
     */
    suspend fun insert(testResult: TestResultDto): Long {
        val testResultId = testResultDao.getTestResultId(testResult.patientId, testResult.collectionDate) ?: -1L
        if (testResultId != -1L) {
            return testResultId
        }
        return testResultDao.insert(testResult.toEntity())
    }

    suspend fun insertAuthenticatedTestResult(testResultDto: TestResultDto): Long {
        return testResultDao.insert(testResultDto.toEntity())
    }
    suspend fun insertAllAuthenticatedTestRecords(recordDtos: List<TestRecordDto>): List<Long> {
        return testResultDao.insertTestRecords(recordDtos.map { it.toEntity() })
    }

    suspend fun insert(testRecords: List<TestRecordDto>): List<Long> {
        return testResultDao.insert(testRecords.map { it.toEntity() })
    }

    suspend fun delete(testResultId: Long): Int = testResultDao.delete(testResultId)

    suspend fun deleteAuthenticatedTestRecords(patientId: Long): Int =
        testResultDao.deleteAuthenticatedTestRecords(patientId)
}
