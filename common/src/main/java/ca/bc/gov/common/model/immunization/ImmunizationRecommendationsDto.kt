package ca.bc.gov.common.model.immunization

import java.time.Instant

data class ImmunizationRecommendationsDto(
    val recommendationSetId: String?,
    var patientId: Long = 0,
    val immunizationName: String?,
    val status: ForecastStatus? = null,
    val diseaseDueDate: Instant?,
)
