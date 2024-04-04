package ca.bc.gov.common.model.immunization

/**
 * @author Pinakin Kansara
 */
data class ImmunizationForecastDto(
    val id: Long = 0,
    var immunizationRecordId: Long = 0,
    val recommendationId: String? = null,
    val status: ForecastStatus? = null,
    val displayName: String? = null,
    val dueDate: String
)
