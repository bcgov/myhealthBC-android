package ca.bc.gov.common.model

data class PatientWithBCSCLoginDto(
    val birthDate: String?,
    val firstName: String?,
    val gender: String?,
    val hdid: String?,
    val lastName: String?,
    val personalHealthNumber: String?
)