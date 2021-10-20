package ca.bc.gov.bchealth.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.model.network.responses.vaccinestatus.VaxStatusResponse
import ca.bc.gov.bchealth.services.ImmunizationServices
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.SHCDecoder
import ca.bc.gov.bchealth.utils.getDateTime
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
    private val shcDecoder: SHCDecoder,
    private val immunizationServices: ImmunizationServices,
) {

    val cards: Flow<List<HealthCardDto>> = dataSource.getCards().map { healthCards ->
        healthCards.map { card ->
            try {
                val data = shcDecoder.getImmunizationStatus(card.uri)
                HealthCardDto(
                    card.id, data.name, data.status, card.uri,
                    false, data.issueDate.getDateTime()
                )
            } catch (e: Exception) {
                HealthCardDto(
                    0, "", ImmunizationStatus.INVALID_QR_CODE, card.uri,
                    false
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

    private val vaxStatusResponseMutableLiveData = MutableLiveData<Response<VaxStatusResponse>>()

    val vaxStatusResponseLiveData: Flow<Response<VaxStatusResponse>>
        get() = vaxStatusResponseMutableLiveData.asFlow()

    suspend fun getVaccineStatus(phn: String, dob: String, dov: String) {
        vaxStatusResponseMutableLiveData.postValue(Response.Loading())
        val result = immunizationServices.getVaccineStatus(
            phn, dob, dov
        )
        if (result.isSuccessful) {
            vaxStatusResponseMutableLiveData.postValue(Response.Success(result.body()))
        } else {
            vaxStatusResponseMutableLiveData.postValue(Response.Error("Error!"))
        }
    }
}
