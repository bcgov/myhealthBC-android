package ca.bc.gov.common.model.hospitalvisits

data class HospitalVisitDto(
    val id: Long = 0,
    var patientId: Long = -1,
    val healthService: String,
    val location: String,
    val provider: String,
    val visitType: String,
    val visitDate: String,
    val dischargeDate: String?,
    val encounterId: String?
)
