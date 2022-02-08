package ca.bc.gov.data.remote.model.base

import com.google.gson.annotations.SerializedName

data class AuthenticatedCovidTestPayload(
    val loaded: Boolean,
    val orders: List<Order>,
    @SerializedName("retryin")
    val retryInMilli: Long
)