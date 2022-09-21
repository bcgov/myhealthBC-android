package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.banner.BannerPayload
import com.google.gson.annotations.SerializedName

data class BannerResponse(
    @SerializedName("resourcePayload")
    val payload: BannerPayload? = null,

    @SerializedName("resultError")
    val error: Error?
)
