package ca.bc.gov.common.model.services

/**
 * @author Pinakin Kansara
 */
enum class OrganDonorStatusDto(val value: String) {
    UNKNOWN("Unknown"),
    REGISTERED("Registered"),
    NOT_REGISTERED("Not Registered"),
    ERROR("Error"),
    PENDING("Pending")
}
