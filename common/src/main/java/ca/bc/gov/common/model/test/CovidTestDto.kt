package ca.bc.gov.common.model.test

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class CovidTestDto(
    val id: String,
    val testType: String?,
    val outOfRange: Boolean,
    val collectedDateTime: Instant,
    val testStatus: String?,
    val labResultOutcome: String?,
    val resultDescription: List<String> = emptyList(),
    val resultLink: String?,
    val receivedDateTime: Instant,
    val resultDateTime: Instant,
    val loInc: String?,
    val loIncName: String?,
    var covidOrderId: String = ""
)
