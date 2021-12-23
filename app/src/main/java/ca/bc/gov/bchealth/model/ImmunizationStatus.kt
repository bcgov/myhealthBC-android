package ca.bc.gov.bchealth.model

/**
 * [ImmunizationStatus]
 *
 * @author Pinakin Kansara
 */
enum class ImmunizationStatus(val value: String) {
    FULLY_IMMUNIZED("Vaccinated"),
    PARTIALLY_IMMUNIZED("Partially vaccinated"),
    INVALID_QR_CODE("No record")
}
