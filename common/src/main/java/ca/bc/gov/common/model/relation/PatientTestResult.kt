package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.patient.Patient
import ca.bc.gov.common.model.test.TestRecord
import ca.bc.gov.common.model.test.TestResult

/**
 * @author Pinakin Kansara
 */
data class PatientTestResult(
    val patient: Patient,
    val testResult: TestResult,
    val records: List<TestRecord>
)
