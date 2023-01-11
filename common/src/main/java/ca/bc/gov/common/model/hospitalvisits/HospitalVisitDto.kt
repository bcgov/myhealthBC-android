package ca.bc.gov.common.model.hospitalvisits

import java.time.Instant

data class HospitalVisitDto(
    val id: Long = 0,
    val patientId: Long,
    val healthService: String,
    val facility: String,
    val location: String,
    val provider: String?,
    val visitType: String,
    val visitDate: Instant,
    val dischargeDate: Instant,
)
