package ca.bc.gov.data.remote.model.base

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class Error(
    @SerializedName("resultMessage")
    val message: String,
    @SerializedName("errorCode")
    val code: String,
    val traceId: String,
    @SerializedName("actionCode")
    val action: Action

)

enum class Action(val code: String) {
    REFRESH("REFRESH")
}
