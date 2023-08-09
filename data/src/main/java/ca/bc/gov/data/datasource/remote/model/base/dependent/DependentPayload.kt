package ca.bc.gov.data.datasource.remote.model.base.dependent

import com.google.gson.annotations.SerializedName

data class DependentPayload(
    @SerializedName("dependentInformation")
    val dependentInformation: DependentInformation,

    @SerializedName("ownerId")
    val ownerId: String,

    @SerializedName("delegateId")
    val delegateId: String,

    @SerializedName("reasonCode")
    val reasonCode: Long,

    @SerializedName("totalDelegateCount")
    val totalDelegateCount: Long,

    @SerializedName("version")
    val version: Long,
)
