package ca.bc.gov.common.model

data class LabResultDto(
    val collectedDateTime: String?,
    val id: String,
    val labResultOutcome: String?,
    val loInc: String?,
    val loIncName: String?,
    val outOfRange: Boolean,
    val receivedDateTime: String,
    val resultDateTime: String,
    val resultDescription: List<String?>,
    val resultLink: String?,
    val testStatus: String?,
    val testType: String?
)