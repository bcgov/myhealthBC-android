package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.Status
import ca.bc.gov.data.datasource.remote.model.base.immunization.ImmunizationPayload
import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class ImmunizationResponse(
    @SerializedName("resourcePayload")
    val payload: ImmunizationPayload,

    @SerializedName("totalResultCount")
    val totalResultCount: Int,

    @SerializedName("pageIndex")
    val pageIndex: Int,

    @SerializedName("pageSize")
    val pageSize: Int,

    @SerializedName("resultStatus")
    val status: Status,

    @SerializedName("resultError")
    val error: Error?
)
