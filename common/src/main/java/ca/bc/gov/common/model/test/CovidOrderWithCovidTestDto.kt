package ca.bc.gov.common.model.test

/**
 * @author Pinakin Kansara
 */
data class CovidOrderWithCovidTestDto(
    val covidOrder: CovidOrderDto,
    val covidTests: List<CovidTestDto> = emptyList()
)
