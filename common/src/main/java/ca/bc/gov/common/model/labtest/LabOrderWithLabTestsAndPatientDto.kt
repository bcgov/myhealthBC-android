package ca.bc.gov.common.model.labtest

import ca.bc.gov.common.model.patient.PatientDto

/**
 * @author Pinakin Kansara
 */
data class LabOrderWithLabTestsAndPatientDto(
    val labOrderWithLabTest: LabOrderWithLabTestDto,
    val patient: PatientDto
)
