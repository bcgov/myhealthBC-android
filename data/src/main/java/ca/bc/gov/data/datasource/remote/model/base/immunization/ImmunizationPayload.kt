package ca.bc.gov.data.datasource.remote.model.base.immunization

/**
 * @author Pinakin Kansara
 */
data class ImmunizationPayload(
    val loadState: LoadState,
    val immunizations: List<ImmunizationRecord> = emptyList(),
    val recommendations: List<Recommendation> = emptyList()
)
