package ca.bc.gov.data.datasource.remote.model.base.hospitalvisit

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class HospitalVisitInformation(
    @SerializedName("encounterId")
    val encounterId: String,

    @SerializedName("facility")
    val facility: String,

    @SerializedName("healthService")
    val healthService: String,

    @SerializedName("visitType")
    val visitType: String,

    @SerializedName("admitDateTime")
    val admitDateTime: Instant,

    @SerializedName("endDateTime")
    val endDateTime: Instant,

    @SerializedName("provider")
    val provider: String?,
)
