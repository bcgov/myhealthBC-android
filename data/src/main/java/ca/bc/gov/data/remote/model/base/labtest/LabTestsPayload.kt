package ca.bc.gov.data.remote.model.base.labtest

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class LabTestsPayload(
    val loaded: Boolean,
    @SerializedName("retryin")
    val retryInMilli: Long,
    val orders: List<LabTestOrder>
)
