package ca.bc.gov.data.datasource.remote.model.base.hospitalvisit

import ca.bc.gov.data.datasource.remote.model.base.Error
import com.google.gson.annotations.SerializedName

class HospitalVisitResponse(
    @SerializedName("resourcePayload")
    val payload: HospitalVisitPayload? = null,

    @SerializedName("resultError")
    val error: Error?
)

data class HospitalVisitPayload(
    @SerializedName("hospitalVisits")
    val list: List<HospitalVisitInformation> = emptyList(),
)

