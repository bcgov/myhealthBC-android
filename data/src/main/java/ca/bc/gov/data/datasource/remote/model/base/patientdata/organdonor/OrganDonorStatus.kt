package ca.bc.gov.data.datasource.remote.model.base.patientdata.organdonor

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
enum class OrganDonorStatus(val value: String) {
    @SerializedName("Unknown")
    UNKNOWN("Unknown"),

    @SerializedName("Registered")
    REGISTERED("Registered"),

    @SerializedName("Not Registered")
    NOT_REGISTERED("Not Registered"),

    @SerializedName("Error")
    ERROR("Error"),

    @SerializedName("Pending")
    PENDING("Pending")
}
