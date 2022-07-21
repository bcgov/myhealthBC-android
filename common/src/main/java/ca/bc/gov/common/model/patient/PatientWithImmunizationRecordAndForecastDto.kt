package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.immunization.ImmunizationRecordWithForecastDto

/**
 * @author Pinakin Kansara
 */
data class PatientWithImmunizationRecordAndForecastDto(
    val patient: PatientDto,
    val immunizationRecords: List<ImmunizationRecordWithForecastDto> = emptyList()
)
