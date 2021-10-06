package ca.bc.gov.bchealth.repository

import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.utils.SHCDecoder
import java.text.SimpleDateFormat
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.Locale

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
                HealthCardDto(
                    card.id, data.name, data.status, card.uri,
                    false, "Issued on " + getDateTime(data.issueDate)
                )
            } catch (e: Exception) {
                HealthCardDto(
                    0, "", ImmunizationStatus.INVALID_QR_CODE, card.uri,
                    false, ""
                )
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
                        if (shcDecoder.getImmunizationStatus(healthCard.uri).status
                            == ImmunizationStatus.PARTIALLY_IMMUNIZED
                        ) {
                            healthCard.uri = card.uri
                            dataSource.update(healthCard)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateHealthCard(card: HealthCard) = dataSource.update(card)
    suspend fun unLink(card: HealthCard) = dataSource.unLink(card)
    suspend fun rearrangeHealthCards(cards: List<HealthCard>) = dataSource.rearrange(cards)

    private fun getDateTime(epochTime: Long): String? {
        return try {
            val date1 = Date(epochTime)
            val format = SimpleDateFormat("MMMM-dd-y, HH:mm", Locale.ENGLISH)
            format.format(date1)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }
    }
}
