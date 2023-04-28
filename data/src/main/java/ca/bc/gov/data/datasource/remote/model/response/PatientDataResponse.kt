package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.patientdata.PatientDataItem
import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 * uses api = v2
 */
data class PatientDataResponse(
    /**
     * Hold multiple type of patient data.
     */
    @SerializedName("items")
    val items: List<PatientDataItem> = emptyList()
)
