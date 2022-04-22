package ca.bc.gov.common.model.immunization

import ca.bc.gov.common.model.patient.PatientDto

/**
 * @author Pinakin Kansara
 */
data class ImmunizationRecordWithForecastAndPatientDto(
    val immunizationRecordWithForecast: ImmunizationRecordWithForecastDto,
    val patient: PatientDto
)
