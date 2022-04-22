package ca.bc.gov.common.model.immunization

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class ImmunizationForecastDto(
    val id: Long = 0,
    var immunizationRecordId: Long = 0,
    val recommendationId: String? = null,
    val createDate: Instant,
    val status: String? = null,
    val displayName: String? = null,
    val eligibleDate: Instant,
    val dueDate: Instant
)
