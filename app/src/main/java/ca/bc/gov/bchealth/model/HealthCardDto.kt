package ca.bc.gov.bchealth.model

/**
 * [HealthCardDto]
 *
 * @author Pinakin Kansara
 */
data class HealthCardDto(
    val id: Int,
    val name: String,
    val status: ImmunizationStatus,
    val uri: String,
    var isExpanded: Boolean = false,
    val issueDate: String?
)
