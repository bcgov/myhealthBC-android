package ca.bc.gov.common.model.clinicaldocument

import java.time.Instant

data class ClinicalDocumentDto(
    val id: Long = 0,
    var patientId: Long = -1,
    val name: String,
    val type: String,
    val facilityName: String,
    val discipline: String,
    val serviceDate: Instant,
    val fileId: String,
)