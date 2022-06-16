package ca.bc.gov.data.datasource.remote.model.base.immunization

/**
 * @author Pinakin Kansara
 */
data class Immunization(
    val name: String?,
    val immunizationAgents: List<ImmunizationAgent> = emptyList()
)
