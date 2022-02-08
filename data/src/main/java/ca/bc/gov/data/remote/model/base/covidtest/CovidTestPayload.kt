package ca.bc.gov.data.remote.model.base.covidtest

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class CovidTestPayload(
    val loaded: Boolean,
    @SerializedName("retryin")
    val retryInMilli: Long,
    @SerializedName("records")
    val covidTestRecords: List<CovidTestRecord>
)
