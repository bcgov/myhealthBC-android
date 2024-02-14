package ca.bc.gov.data.datasource.remote.model.base

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class TermsOfServicePayload(
    val id: String? = null,
    val content: String? = null,
    @SerializedName("effectiveDateTime")
    val effectiveDate: String? = null
)
