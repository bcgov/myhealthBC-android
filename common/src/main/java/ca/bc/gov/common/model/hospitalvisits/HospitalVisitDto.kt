package ca.bc.gov.common.model.hospitalvisits

import java.time.Instant

data class HospitalVisitDto(
    val id: Long = 0,
    var patientId: Long = -1,
    val healthService: String,
    val location: String,
    val provider: String,
    val visitType: String,
    val visitDate: Instant,
    val dischargeDate: Instant?,
    val encounterId: String?
)
