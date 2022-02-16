package ca.bc.gov.common.model

/**
 * @author Pinakin Kansara
 */
data class DispensingPharmacyDto(
    val id: Long,
    val medicationRecordId: Long,
    val pharmacyId: String?,
    val name: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val province: String?,
    val postalCode: String?,
    val countryCode: String?,
    val phoneNumber: String?,
    val faxNumber: String?
)
