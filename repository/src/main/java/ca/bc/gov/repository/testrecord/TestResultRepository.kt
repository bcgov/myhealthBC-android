package ca.bc.gov.repository.testrecord

import ca.bc.gov.data.datasource.local.TestResultLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class TestResultRepository @Inject constructor(
    private val localDataSource: TestResultLocalDataSource
) {

    suspend fun delete(testResultId: Long): Int = localDataSource.delete(testResultId)

    suspend fun deletePatientTestRecords(patientId: Long): Int =
        localDataSource.deletePatientTestRecords(patientId)
}
