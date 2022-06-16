package ca.bc.gov.data.datasource.remote.model.base.immunization

/**
 * @author Pinakin Kansara
 */
data class Forecast(
    val recommendationId: String?,
    val createDate: String,
    val status: String?,
    val displayName: String?,
    val eligibleDate: String,
    val dueDate: String
)
