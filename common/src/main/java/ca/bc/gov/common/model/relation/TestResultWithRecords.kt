package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.test.TestRecord
import ca.bc.gov.common.model.test.TestResult

/**
 * @author Pinakin Kansara
 */
data class TestResultWithRecords(
    val testResult: TestResult,
    val testRecords: List<TestRecord> = emptyList()
)
