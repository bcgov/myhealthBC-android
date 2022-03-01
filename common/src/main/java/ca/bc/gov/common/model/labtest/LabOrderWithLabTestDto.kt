package ca.bc.gov.common.model.labtest

/**
 * @author Pinakin Kansara
 */
data class LabOrderWithLabTestDto(
    val labOrder: LabOrderDto,
    val labTests: List<LabTestDto> = emptyList()
)
