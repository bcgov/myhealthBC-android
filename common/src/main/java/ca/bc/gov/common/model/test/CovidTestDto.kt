package ca.bc.gov.common.model.test

/**
 * @author Pinakin Kansara
 */
data class CovidTestDto(
    val id: String,
    val testType: String?,
    val outOfRange: Boolean,
    val collectedDateTime: String,
    val testStatus: String?,
    val labResultOutcome: String?,
    val resultDescription: List<String> = emptyList(),
    val resultLink: String?,
    val loInc: String?,
    val loIncName: String?,
)
