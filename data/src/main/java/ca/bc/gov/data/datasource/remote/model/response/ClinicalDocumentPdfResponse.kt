package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.Error
import ca.bc.gov.data.datasource.remote.model.base.vaccine.Media
import com.google.gson.annotations.SerializedName

data class ClinicalDocumentPdfResponse(
    @SerializedName("resourcePayload")
    val resourcePayload: Media?,

    @SerializedName("resultError")
    val error: Error?,
)
