package ca.bc.gov.data.datasource.remote.model.base.immunization

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class Immunization(
    @SerializedName("name")
    val name: String?,

    @SerializedName("immunizationAgents")
    val immunizationAgents: List<ImmunizationAgent> = emptyList()
)
