package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.labtest.LabOrderWithLabTestDto

/**
 * @author Pinakin Kansara
 */
data class PatientWithLabOrderAndLatTestsDto(
    val patient: PatientDto,
    val labOrdersWithLabTests: List<LabOrderWithLabTestDto> = emptyList()
)
