package ca.bc.gov.common.model

/**
 * @author Pinakin Kansara
 */
enum class ImmunizationStatus(val status: Int) {
    PARTIALLY_IMMUNIZED(1),
    FULLY_IMMUNIZED(2),
    INVALID(3)
}
