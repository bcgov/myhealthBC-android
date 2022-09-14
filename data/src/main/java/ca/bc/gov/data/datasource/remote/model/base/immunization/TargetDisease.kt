package ca.bc.gov.data.datasource.remote.model.base.immunization

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class TargetDisease(
    @SerializedName("code")
    val code: String?,

    @SerializedName("name")
    val name: String?
)
