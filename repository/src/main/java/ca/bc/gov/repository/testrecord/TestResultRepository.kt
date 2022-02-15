package ca.bc.gov.repository.testrecord

import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto
import ca.bc.gov.data.datasource.TestResultLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TestResultRepository @Inject constructor(
    private val localDataSource: TestResultLocalDataSource
) {

    suspend fun insertTestResult(testResultDto: TestResultDto): Long =
        localDataSource.insert(testResultDto)

    suspend fun insertAuthenticatedTestResult(testResultDto: TestResultDto): Long =
        localDataSource.insertAuthenticatedTestResult(testResultDto)

    suspend fun insertAllAuthenticatedTestRecords(recordDtos: List<TestRecordDto>): List<Long> =
        localDataSource.insertAllAuthenticatedTestRecords(recordDtos)

    suspend fun delete(testResultId: Long): Int = localDataSource.delete(testResultId)

    suspend fun insertAllTestRecords(recordDtos: List<TestRecordDto>): List<Long> =
        localDataSource.insert(recordDtos)
}
