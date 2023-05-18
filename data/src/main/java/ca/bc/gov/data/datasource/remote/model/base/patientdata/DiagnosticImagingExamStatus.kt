package ca.bc.gov.data.datasource.remote.model.base.patientdata

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
enum class DiagnosticImagingExamStatus(val value: String) {
    @SerializedName("Unknown")
    UNKNOWN("Unknown"),

    @SerializedName("Scheduled")
    SCHEDULED("Scheduled"),

    @SerializedName("In Progress")
    IN_PROGRESS("In Progress"),

    @SerializedName("Finalized")
    FINALIZED("Finalized"),

    @SerializedName("Pending")
    PENDING("Pending"),

    @SerializedName("Completed")
    COMPLETED("Completed"),

    @SerializedName("Amended")
    AMENDED("Amended")
}
