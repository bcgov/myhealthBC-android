package ca.bc.gov.data.datasource.remote.model.request

import com.google.gson.annotations.SerializedName

data class FeedbackRequest(
    @SerializedName("comment")
    val message: String,
)
