package ca.bc.gov.repository.testrecord

import ca.bc.gov.common.model.test.TestRecord
import ca.bc.gov.data.datasource.TestRecordLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TestRecordRepository @Inject constructor(
    private val localDataSource: TestRecordLocalDataSource
) {

    suspend fun insertAllTestRecords(records: List<TestRecord>): List<Long> =
        localDataSource.insertAllTestRecords(records)
}
