package ca.bc.gov.data.datasource.remote.model.base.hospitalvisit

import com.google.gson.annotations.SerializedName

data class HospitalVisitInformation(
    @SerializedName("encounterId")
    val encounterId: String,

    @SerializedName("facility")
    val facility: String,

    @SerializedName("healthService")
    val healthService: String?,

    @SerializedName("visitType")
    val visitType: String?,

    @SerializedName("admitDateTime")
    val admitDateTime: String,

    @SerializedName("endDateTime")
    val endDateTime: String?,

    @SerializedName("provider")
    val provider: String?,
)
