package ca.bc.gov.data.remote.model.base

import com.google.gson.annotations.SerializedName

data class LabResult(
    val collectedDateTime: String?,
    val id: String,
    val labResultOutcome: String?,
    @SerializedName("loinc")
    val loInc: String?,
    @SerializedName("loincName")
    val loIncName: String?,
    val outOfRange: Boolean,
    val receivedDateTime: String,
    val resultDateTime: String,
    val resultDescription: List<String>,
    val resultLink: String = "",
    val testStatus: String?,
    val testType: String?
)
