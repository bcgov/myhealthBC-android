package ca.bc.gov.data.datasource

import ca.bc.gov.common.model.relation.TestResultWithRecordsDto
import ca.bc.gov.common.model.test.TestResultDto
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
     * @param testResultDto
     * @return return resultId or -1L
     */
    suspend fun insertTestResult(testResultDto: TestResultDto): Long {
        val testResultId = testResultDao.insertTestResult(testResultDto.toEntity())
        if (testResultId == -1L) {
            return testResultDao.getTestResultId(testResultDto.patientId, testResultDto.collectionDate) ?: -1L
        }
        return testResultId
    }

    suspend fun getTestResults(patientId: Long): List<TestResultDto> =
        testResultDao.getTestResults(patientId).map {
            it.toDto()
        }

    suspend fun getTestResultWithRecords(testResultId: Long): TestResultWithRecordsDto =
        testResultDao.getTestResultWithRecord(testResultId).toDto()

    suspend fun delete(testResultId: Long): Int = testResultDao.delete(testResultId)
}
