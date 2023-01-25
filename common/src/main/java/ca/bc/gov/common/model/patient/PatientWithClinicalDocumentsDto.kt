package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto

data class PatientWithClinicalDocumentsDto(
    val patient: PatientDto,
    val clinicalDocuments: List<ClinicalDocumentDto> = emptyList()
)
