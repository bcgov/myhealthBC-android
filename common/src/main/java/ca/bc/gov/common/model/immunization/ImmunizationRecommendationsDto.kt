package ca.bc.gov.common.model.immunization

data class ImmunizationRecommendationsDto(
    val recommendationSetId: String?,
    var patientId: Long = 0,
    val immunizationName: String?,
    val status: String?,
    val diseaseDueDate: String?,
)
