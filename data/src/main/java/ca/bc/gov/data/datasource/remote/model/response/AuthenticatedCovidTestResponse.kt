package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.AuthenticatedCovidTestPayload
import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.Status
import com.google.gson.annotations.SerializedName

data class AuthenticatedCovidTestResponse(
    @SerializedName("resourcePayload")
    val payload: AuthenticatedCovidTestPayload,
    val totalResultCount: Int,
    val pageIndex: Int,
    val pageSize: Int,
    @SerializedName("resultStatus")
    val status: Status,
    @SerializedName("resultError")
    val error: Error?
)
