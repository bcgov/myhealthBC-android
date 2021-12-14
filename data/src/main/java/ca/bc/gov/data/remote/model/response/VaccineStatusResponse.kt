package ca.bc.gov.data.remote.model.response

import ca.bc.gov.data.remote.model.base.Error
import ca.bc.gov.data.remote.model.base.Status
import ca.bc.gov.data.remote.model.base.VaccineResourcePayload
import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class VaccineStatusResponse(
    @SerializedName("resourcePayload")
    val payload: VaccineResourcePayload,
    val totalResultCount: Int,
    val pageIndex: Int,
    val pageSize: Int,
    @SerializedName("resultStatus")
    val status: Status,
    @SerializedName("resultError")
    val error: Error?
)
