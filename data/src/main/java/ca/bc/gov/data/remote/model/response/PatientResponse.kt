package ca.bc.gov.data.remote.model.response

import ca.bc.gov.data.remote.model.base.Error
import ca.bc.gov.data.remote.model.base.PatientPayload
import ca.bc.gov.data.remote.model.base.Status
import com.google.gson.annotations.SerializedName

data class PatientResponse(
    @SerializedName("resourcePayload")
    val payload: PatientPayload?,
    val totalResultCount: Int?,
    val pageIndex: Int?,
    val pageSize: Int?,
    @SerializedName("resultStatus")
    val status: Status,
    @SerializedName("resultError")
    val error: Error?
)
