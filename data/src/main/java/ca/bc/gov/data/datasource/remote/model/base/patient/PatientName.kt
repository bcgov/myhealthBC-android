package ca.bc.gov.data.datasource.remote.model.base.patient

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class PatientName(
    val givenName: String?,
    @SerializedName("surname")
    val surName: String?
)
