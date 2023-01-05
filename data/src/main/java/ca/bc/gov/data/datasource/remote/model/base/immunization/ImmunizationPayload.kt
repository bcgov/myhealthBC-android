package ca.bc.gov.data.datasource.remote.model.base.immunization

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class ImmunizationPayload(
    @SerializedName("immunizations")
    val immunizations: List<ImmunizationRecord> = emptyList(),

    @SerializedName("recommendations")
    val recommendations: List<Recommendation> = emptyList()
)
