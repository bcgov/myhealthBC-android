package ca.bc.gov.data.datasource.remote.model.base.covidtest

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class CovidTestPayload(
    @SerializedName("records")
    val covidTestRecords: List<CovidTestRecord>
)
