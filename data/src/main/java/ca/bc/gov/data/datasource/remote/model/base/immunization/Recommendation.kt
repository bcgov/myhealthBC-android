package ca.bc.gov.data.datasource.remote.model.base.immunization

import com.google.gson.annotations.SerializedName

/**
 * @author Pinakin Kansara
 */
data class Recommendation(
    @SerializedName("recommendationSetId")
    val recommendationSetId: String?,

    @SerializedName("diseaseEligibleDate")
    val diseaseEligibleDate: String?,

    @SerializedName("agentEligibleDate")
    val agentEligibleDate: String?,

    @SerializedName("agentDueDate")
    val agentDueDate: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("targetDiseases")
    val targetDiseases: List<TargetDisease> = emptyList(),

    @SerializedName("immunization")
    val immunization: Immunization,

    @SerializedName("recommendedVaccinations")
    val recommendedVaccinations: String?

)
