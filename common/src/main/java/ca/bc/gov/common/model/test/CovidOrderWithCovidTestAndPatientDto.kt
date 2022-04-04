package ca.bc.gov.common.model.test

import ca.bc.gov.common.model.patient.PatientDto

/**
 * @author Pinakin Kansara
 */
data class CovidOrderWithCovidTestAndPatientDto(
    val covidOrderWithCovidTest: CovidOrderWithCovidTestDto,
    val patient: PatientDto
)
