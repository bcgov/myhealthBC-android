package ca.bc.gov.data.datasource.remote.model.base

import com.google.gson.annotations.SerializedName

data class LabResult(
    val id: String,
    val testType: String?,
    val outOfRange: Boolean,
    val collectedDateTime: String,
    val testStatus: String?,
    val labResultOutcome: String?,
    val resultDescription: List<String>,
    val resultLink: String?,
    val receivedDateTime: String,
    val resultDateTime: String,
    @SerializedName("loinc")
    val loInc: String?,
    @SerializedName("loincName")
    val loIncName: String?
)
