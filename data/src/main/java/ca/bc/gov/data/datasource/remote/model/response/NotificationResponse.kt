package ca.bc.gov.data.datasource.remote.model.response

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("categoryName")
    val category: String,

    @SerializedName("displayText")
    val displayText: String,

    @SerializedName("actionUrl")
    val actionUrl: String,

    @SerializedName("actionType")
    val actionType: String,

    @SerializedName("scheduledDateTimeUtc")
    val date: String,
)
