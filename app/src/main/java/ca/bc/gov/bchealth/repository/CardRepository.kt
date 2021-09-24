package ca.bc.gov.bchealth.repository

import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.utils.SHCDecoder
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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
            try {
                val data = shcDecoder.getImmunizationStatus(card.uri)
                HealthCardDto(card.id, data.name, data.status, card.uri)
            } catch (e: Exception) {
                HealthCardDto(0, "", ImmunizationStatus.INVALID_QR_CODE, card.uri)
            }
        }
    }

    suspend fun insert(card: HealthCard) {
        try {
            val cardToBeInserted = shcDecoder.getImmunizationStatus(card.uri)

            val cards = dataSource.getCards().firstOrNull()
            if (cards.isNullOrEmpty()) {
                dataSource.insert(card)
            } else {

                val record = cards.filter { record ->
                    val immunizationRecord = shcDecoder.getImmunizationStatus(record.uri)
                    (
                        immunizationRecord.name == cardToBeInserted.name &&
                            immunizationRecord.birthDate == cardToBeInserted.birthDate
                        )
                }

                if (record.isNullOrEmpty()) {
                    dataSource.insert(card)
                } else {
                    record.forEach { healthCard ->
                        healthCard.uri = card.uri
                        dataSource.update(healthCard)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    suspend fun insertHealthCard(card: HealthCard) = dataSource.insert(card)
    suspend fun updateHealthCard(card: HealthCard) = dataSource.update(card)
    suspend fun unLink(card: HealthCard) = dataSource.unLink(card)
}
