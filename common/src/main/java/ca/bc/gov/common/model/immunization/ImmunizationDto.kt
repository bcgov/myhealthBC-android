package ca.bc.gov.common.model.immunization

data class ImmunizationDto(
    val records: List<ImmunizationRecordWithForecastDto>,
    val recommendations: List<ImmunizationRecommendationsDto>
)
