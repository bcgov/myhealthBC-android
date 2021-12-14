package ca.bc.gov.common.model.test

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class TestRecord(
    var id: String,
    var testResultId: Long = 0,
    val labName: String,
    val collectionDateTime: Instant,
    val resultDateTime: Instant,
    val testName: String,
    val testType: String,
    val testOutcome: String,
    val testStatus: String,
    val resultTitle: String,
    val resultDescription: List<String>,
    val resultLink: String
)
