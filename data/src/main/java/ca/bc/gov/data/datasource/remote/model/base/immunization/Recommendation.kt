package ca.bc.gov.data.datasource.remote.model.base.immunization

/**
 * @author Pinakin Kansara
 */
data class Recommendation(
    val recommendationSetId: String?,
    val disseaseEligibleDate: String?,
    val diseaseDueDate: String?,
    val agentEligibleDate: String?,
    val agentDueDate: String?,
    val status: String?,
    val targetDiseases: List<TargetDisease> = emptyList(),
    val immunization: Immunization
)
