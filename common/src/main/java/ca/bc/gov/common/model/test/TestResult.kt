package ca.bc.gov.common.model.test

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class TestResult(
    var id: Long = 0,
    var patientId: Long = 0,
    val collectionDate: Instant,
    val testRecords: List<TestRecord> = emptyList()
)
