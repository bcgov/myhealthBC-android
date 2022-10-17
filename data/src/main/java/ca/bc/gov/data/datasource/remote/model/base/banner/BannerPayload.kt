package ca.bc.gov.data.datasource.remote.model.base.banner

import com.google.gson.annotations.SerializedName

data class BannerPayload(
    @SerializedName("subject")
    val title: String,

    @SerializedName("text")
    val body: String,

    @SerializedName("effectiveDateTime")
    val startDate: String,

    @SerializedName("expiryDateTime")
    val endDate: String,
)
