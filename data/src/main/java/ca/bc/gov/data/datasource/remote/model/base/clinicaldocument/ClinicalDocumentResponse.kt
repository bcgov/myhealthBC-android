package ca.bc.gov.data.datasource.remote.model.base.clinicaldocument

import ca.bc.gov.data.datasource.remote.model.base.Error
import com.google.gson.annotations.SerializedName

class ClinicalDocumentResponse(
    @SerializedName("resourcePayload")
    val payload: List<ClinicalDocumentPayload> = emptyList(),

    @SerializedName("resultError")
    val error: Error?
)

data class ClinicalDocumentPayload(
    @SerializedName("id")
    val payloadId: String,

    @SerializedName("fileId")
    val fileId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("facilityName")
    val facilityName: String,

    @SerializedName("discipline")
    val discipline: String,

    @SerializedName("serviceDate")
    val serviceDate: String,
)
