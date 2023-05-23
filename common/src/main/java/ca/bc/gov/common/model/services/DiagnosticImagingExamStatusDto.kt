package ca.bc.gov.common.model.services

/**
 * @author Pinakin Kansara
 */
enum class DiagnosticImagingExamStatusDto(val value: String) {
    UNKNOWN("Unknown"),
    SCHEDULED("Scheduled"),
    IN_PROGRESS("In Progress"),
    FINALIZED("Finalized"),
    PENDING("Pending"),
    COMPLETED("Completed"),
    AMENDED("Amended")
}
