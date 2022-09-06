package ca.bc.gov.data.datasource.remote.model.base.immunization

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class ImmunizationAgent(
    @SerializedName("code")
    val code: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("lotNumber")
    val lotNumber: String?,

    @SerializedName("productName")
    val productName: String?
)
