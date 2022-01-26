package ca.bc.gov.repository.testrecord

import ca.bc.gov.common.model.relation.TestResultWithRecordsDto
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
        localDataSource.insertTestResult(testResultDto)

    suspend fun getTestResults(patientId: Long): List<TestResultDto> =
        localDataSource.getTestResults(patientId)

    suspend fun getTestResultWithRecords(testResultId: Long): TestResultWithRecordsDto =
        localDataSource.getTestResultWithRecords(testResultId)

    suspend fun delete(testResultId: Long): Int = localDataSource.delete(testResultId)
}
