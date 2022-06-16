package ca.bc.gov.common.model.immunization

/**
 * @author Pinakin Kansara
 */
data class ImmunizationRecordWithForecastDto(
    val immunizationRecord: ImmunizationRecordDto,
    val immunizationForecast: ImmunizationForecastDto? = null
)
