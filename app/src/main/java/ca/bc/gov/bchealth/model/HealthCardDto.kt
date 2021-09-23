package ca.bc.gov.bchealth.model

import ca.bc.gov.bchealth.data.local.entity.CardType

/**
 * [HealthCardDto]
 *
 * @author Pinakin Kansara
 */
data class HealthCardDto(
    val name: String,
    val status: ImmunizationStatus,
    val uri: String,
    val type: CardType,
    var isExpanded: Boolean
)
