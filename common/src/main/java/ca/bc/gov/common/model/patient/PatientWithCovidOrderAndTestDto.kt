package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto

/**
 * @author Pinakin Kansara
 */
data class PatientWithCovidOrderAndTestDto(
    val patient: PatientDto,
    val covidOrderAndTests: List<CovidOrderWithCovidTestDto> = emptyList()
)
