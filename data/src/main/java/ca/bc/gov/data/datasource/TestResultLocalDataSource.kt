package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.relation.TestResultWithRecords
import ca.bc.gov.common.model.test.TestResult
import ca.bc.gov.data.local.dao.TestResultDao
import ca.bc.gov.data.model.mapper.toDto
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
    suspend fun insertTestResult(testResult: TestResult): Long {
        val testResultId = testResultDao.insertTestResult(testResult.toEntity())
        if (testResultId == -1L) {
            return testResultDao.getTestResultId(testResult.patientId) ?: -1L
        }
        return testResultId
    }

    suspend fun getTestResults(patientId: Long): List<TestResult> =
        testResultDao.getTestResults(patientId).map {
            it.toDto()
        }

    suspend fun getTestResultWithRecords(testResultId: Long): TestResultWithRecords =
        testResultDao.getTestResultWithRecord(testResultId).toDto()

    suspend fun delete(testResultId: Long): Int = testResultDao.delete(testResultId)
}