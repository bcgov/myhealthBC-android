package ca.bc.gov.repository.testrecord

import ca.bc.gov.common.model.relation.TestResultWithRecords
import ca.bc.gov.common.model.test.TestResult
import ca.bc.gov.data.datasource.TestResultLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TestResultRepository @Inject constructor(
    private val localDataSource: TestResultLocalDataSource
) {

    suspend fun insertTestResult(testResult: TestResult): Long =
        localDataSource.insertTestResult(testResult)

    suspend fun getTestResults(patientId: Long): List<TestResult> =
        localDataSource.getTestResults(patientId)

    suspend fun getTestResultWithRecords(testResultId: Long): TestResultWithRecords =
        localDataSource.getTestResultWithRecords(testResultId)

    suspend fun delete(testResultId: Long): Int = localDataSource.delete(testResultId)
}