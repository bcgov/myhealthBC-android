package ca.bc.gov.data.datasource.remote.model.base.specialauthority

import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.Status
import com.google.gson.annotations.SerializedName

/**
 * @author: Created by Rashmi Bambhania on 24,June,2022
 */
data class SpecialAuthorityResponse(
    val pageIndex: Int,
    val pageSize: Int,
    @SerializedName("resourcePayload")
    val payload: List<SpecialAuthorityPayload> = emptyList(),
    @SerializedName("resultStatus")
    val status: Status,
    @SerializedName("resultError")
    val error: Error?,
    val totalResultCount: Int
)
