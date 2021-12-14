package ca.bc.gov.repository

import ca.bc.gov.common.model.relation.PatientTestResult
import ca.bc.gov.repository.model.mapper.toCreatePatientDto
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

    suspend fun insertTestResult(patientTestResult: PatientTestResult) {
        val patientId =
            patientRepository.insertPatient(patientTestResult.patient.toCreatePatientDto())
        val testResult = patientTestResult.testResult
        testResult.patientId = patientId
        val testResultId = testResultRepository.insertTestResult(testResult)
        val records = patientTestResult.records
        records.forEach { testRecord ->
            testRecord.testResultId = testResultId
        }
        testRecordRepository.insertAllTestRecords(records)
    }

    suspend fun getPatientWithTestResult(patientId: Long,testResultId: Long): PatientTestResult {
        val patient = patientRepository.getPatient(patientId)
        val testResultWithRecords = testResultRepository.getTestResultWithRecords(testResultId)
        return PatientTestResult(patient,testResultWithRecords.testResult,testResultWithRecords.testRecords)
    }
}