package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.test.TestResultDto
import ca.bc.gov.data.local.dao.TestResultDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TestResultLocalDataSource @Inject constructor(
    private val testResultDao: TestResultDao
) {

    /**
     * @param testResultDto
     * @return return resultId or -1L
     */
    suspend fun insertTestResult(testResultDto: TestResultDto): Long {
        val testResultId = testResultDao.getTestResultId(testResultDto.patientId, testResultDto.collectionDate) ?: -1L
        if (testResultId != -1L) {
            return testResultId
        }
        return testResultDao.insertTestResult(testResultDto.toEntity())
    }

    suspend fun insertAuthenticatedTestResult(testResultDto: TestResultDto): Long {
        return testResultDao.insertTestResult(testResultDto.toEntity())
    }

    suspend fun delete(testResultId: Long): Int = testResultDao.delete(testResultId)
}
