package ca.bc.gov.data.datasource.remote.model.base.healthvisits

import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.Status
import com.google.gson.annotations.SerializedName

/**
 * @author: Created by Rashmi Bambhania on 20,June,2022
 */
class HealthVisitsResponse(
    @SerializedName("resourcePayload")
    val payload: List<HealthVisitsPayload> = emptyList(),
    val totalResultCount: Int,
    val pageIndex: Int,
    val pageSize: Int,
    @SerializedName("resultStatus")
    val status: Status,
    @SerializedName("resultError")
    val error: Error?
)
