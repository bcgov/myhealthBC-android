package ca.bc.gov.data.datasource.remote.model.base.covidtest

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class CovidTestRecord(
    @SerializedName("patientDisplayName")
    val name: String,
    @SerializedName("lab")
    val labName: String,
    val reportId: String,
    val collectionDateTime: String,
    val resultDateTime: String,
    val testName: String,
    val testType: String,
    val testStatus: String,
    val testOutcome: String,
    val resultTitle: String,
    val resultDescription: List<String>,
    val resultLink: String
)
