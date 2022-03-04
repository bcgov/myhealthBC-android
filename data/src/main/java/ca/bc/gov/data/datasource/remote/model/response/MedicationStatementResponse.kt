package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.Status
import ca.bc.gov.data.datasource.remote.model.base.medication.MedicationStatementPayload
import com.google.gson.annotations.SerializedName

data class MedicationStatementResponse(
    val pageIndex: Int? = 0,
    val pageSize: Int? = 0,
    @SerializedName("resourcePayload")
    val payload: List<MedicationStatementPayload>?,
    @SerializedName("resultError")
    val error: Error?,
    @SerializedName("resultStatus")
    val status: Status,
    val totalResultCount: Int? = 0
)
