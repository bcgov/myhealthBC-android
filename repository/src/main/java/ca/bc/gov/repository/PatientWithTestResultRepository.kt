package ca.bc.gov.repository

import ca.bc.gov.common.model.DataSource
import ca.bc.gov.common.model.relation.PatientTestResultDto
import ca.bc.gov.common.model.relation.PatientWithTestRecordDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsDto
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

    suspend fun insertTestResult(patientTestResult: PatientTestResultDto): Long {
        val patientId =
            patientRepository.insertPatient(patientTestResult.patientDto)
        val testResult = patientTestResult.testResultDto
        testResult.patientId = patientId
        val testResultId = testResultRepository.insertTestResult(testResult)
        val records = patientTestResult.recordDtos
        records.forEach { testRecord ->
            testRecord.testResultId = testResultId
        }
        testRecordRepository.insertAllTestRecords(testResultId, records)
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
        testRecordRepository.insertAllAuthenticatedTestRecords(testResultId, records)
        return testResultId
    }

    suspend fun getPatientWithTestResult(
        patientId: Long,
        testResultId: Long
    ): PatientTestResultDto {
        val patient = patientRepository.getPatient(patientId)
        val testResultWithRecords = testResultRepository.getTestResultWithRecords(testResultId)
        return PatientTestResultDto(
            patient,
            testResultWithRecords.testResultDto,
            testResultWithRecords.testRecordDtos
        )
    }

    suspend fun getPatientWithTestRecords(patientId: Long): PatientWithTestRecordDto {
        val patient = patientRepository.getPatient(patientId)
        val testResult = testResultRepository.getTestResults(patientId)
        val resultWithRecords = testResult.map { result ->
            val testRecords = testRecordRepository.getTestRecords(result.id)
            TestResultWithRecordsDto(result, testRecords)
        }
        return PatientWithTestRecordDto(patient, resultWithRecords)
    }
}
