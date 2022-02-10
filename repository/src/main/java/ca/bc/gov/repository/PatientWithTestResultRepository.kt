package ca.bc.gov.repository

import ca.bc.gov.common.model.DataSource
import ca.bc.gov.common.model.relation.PatientWithTestResultsAndRecordsDto
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.testrecord.TestRecordRepository
import ca.bc.gov.repository.testrecord.TestResultRepository
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientWithTestResultRepository @Inject constructor(
    private val patientRepository: PatientRepository,
    private val testResultRepository: TestResultRepository,
    private val testRecordRepository: TestRecordRepository
) {

    suspend fun insertTestResult(patientWithTestResultsAndRecords: PatientWithTestResultsAndRecordsDto): Long {
        val patientId =
            patientRepository.insert(patientWithTestResultsAndRecords.patient)
        val testResult = patientWithTestResultsAndRecords.testResultWithRecords
        var testResultId = -1L
        testResult.forEach {
            it.testResult.patientId = patientId
            testResultId = testResultRepository.insertTestResult(it.testResult)
            val records = it.testRecords
            records.forEach { testRecord ->
                testRecord.testResultId = testResultId
            }
            testRecordRepository.insertAllTestRecords(records)
        }
        return testResultId
    }

    suspend fun insertAuthenticatedTestResult(patientId: Long, testResultWithRecordsDto: TestResultWithRecordsDto): Long {
        val testResult = testResultWithRecordsDto.testResultDto
        testResult.patientId = patientId
        testResult.dataSource = DataSource.BCSC
        val testResultId = testResultRepository.insertAuthenticatedTestResult(testResult)
        val records = testResultWithRecordsDto.testRecordDtos
        records.forEach { testRecord ->
            testRecord.testResultId = testResultId
        }
        testRecordRepository.insertAllAuthenticatedTestRecords(records)
        return testResultId
    }
}
