package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.Status
import ca.bc.gov.data.datasource.remote.model.base.profile.UserProfilePayload
import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class UserProfileResponse(
    @SerializedName("resourcePayload")
    val resourcePayload: UserProfilePayload,
    val totalResultCount: Int?,
    val pageIndex: Int?,
    val pageSize: Int?,
    @SerializedName("resultStatus")
    val status: Status,
    @SerializedName("resultError")
    val error: Error?
)
