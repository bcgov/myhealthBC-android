package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.dependent.DependentPayload
import com.google.gson.annotations.SerializedName

data class DependentListResponse(
    @SerializedName("resourcePayload")
    val payload: List<DependentPayload>? = null,

    @SerializedName("resultError")
    val error: Error? = null
)
