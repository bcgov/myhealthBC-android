package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.ApiWarning
import ca.bc.gov.data.datasource.remote.model.base.patient.PatientPayload
import com.google.gson.annotations.SerializedName

data class PatientResponse(
    @SerializedName("warning")
    val apiWarning: ApiWarning?,
    @SerializedName("resourcePayload")
    val payload: PatientPayload?,
)
