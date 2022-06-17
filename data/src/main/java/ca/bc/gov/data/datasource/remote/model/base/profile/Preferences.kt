package ca.bc.gov.data.datasource.remote.model.base.profile

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class Preferences(
    @SerializedName("hdId") val hdid: String? = null,
    val preference: String? = null,
    val value: String? = null,
    val version: Int,
    val createdDateTime: String,
    val createdBy: String? = null,
    val updatedDateTime: String,
    val updatedBy: String? = null
)
