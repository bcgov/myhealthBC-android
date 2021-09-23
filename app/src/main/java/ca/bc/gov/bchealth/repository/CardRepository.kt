package ca.bc.gov.bchealth.repository

import ca.bc.gov.bchealth.data.local.entity.CardType
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.utils.SHCDecoder
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [CardRepository]
 *
 * @author Pinakin Kansara
 */
class CardRepository @Inject constructor(
    private val dataSource: LocalDataSource,
    private val shcDecoder: SHCDecoder
) {

    val cards: Flow<List<HealthCardDto>> = dataSource.getCards().map { healthCards ->
        healthCards.map { card ->
            if (card.type == CardType.QR) {
                val data = shcDecoder.getImmunizationStatus(card.uri)
                HealthCardDto(data.first, data.second, card.uri, card.type, false)
            } else {
                HealthCardDto("", ImmunizationStatus.FULLY_IMMUNIZED, card.uri, card.type, false)
            }
        }
    }

    suspend fun insertHealthCard(card: HealthCard) = dataSource.insert(card)
}
