package ca.bc.gov.common.model

data class PatientAddressDto(
    val streetLines: List<String>,
    val city: String,
    val state: String,
    val postalCode: String,
)
