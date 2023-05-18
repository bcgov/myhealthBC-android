package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.services.DiagnosticImagingDataDto

/**
 * @author Pinakin Kansara
 */
data class PatientWithDataDto(
    val patient: PatientDto,
    val diagnosticImagingDataList: List<DiagnosticImagingDataDto>
)
