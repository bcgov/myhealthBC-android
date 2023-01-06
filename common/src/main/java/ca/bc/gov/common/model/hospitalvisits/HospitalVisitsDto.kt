package ca.bc.gov.common.model.hospitalvisits

import java.time.Instant

data class HospitalVisitsDto(
    val id: Long = 0,
    val patientId: Long,
    val facility: String,
    val location: String,
    val provider: String?,
    val visitType: String,
    val visitDate: Instant,
    val dischargeDate: Instant,
)
