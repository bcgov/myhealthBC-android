package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.TestRecord
import ca.bc.gov.common.model.test.TestResult

/**
 * @author Pinakin Kansara
 */
data class PatientTestResult(
    val patientDto: PatientDto,
    val testResult: TestResult,
    val records: List<TestRecord>
)
