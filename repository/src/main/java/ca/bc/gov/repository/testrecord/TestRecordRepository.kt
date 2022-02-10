package ca.bc.gov.repository.testrecord

import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.data.datasource.TestRecordLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TestRecordRepository @Inject constructor(
    private val localDataSource: TestRecordLocalDataSource
) {

    suspend fun insertAllTestRecords(recordDtos: List<TestRecordDto>): List<Long> =
        localDataSource.insertAllTestRecords(recordDtos)

    suspend fun insertAllAuthenticatedTestRecords(recordDtos: List<TestRecordDto>): List<Long> =
        localDataSource.insertAllAuthenticatedTestRecords(recordDtos)

    suspend fun getTestRecords(testResultId: Long): List<TestRecordDto> =
        localDataSource.getTestRecords(testResultId)
}
