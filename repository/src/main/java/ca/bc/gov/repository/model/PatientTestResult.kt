package ca.bc.gov.repository.model

import ca.bc.gov.common.model.patient.Patient
import ca.bc.gov.common.model.test.TestResult

/**
 * @author Pinakin Kansara
 */
data class PatientTestResult(
    val patient: Patient,
    val testResult: TestResult
)
