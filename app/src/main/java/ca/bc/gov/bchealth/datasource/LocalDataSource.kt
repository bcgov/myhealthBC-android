package ca.bc.gov.bchealth.datasource

import ca.bc.gov.bchealth.data.local.BcVaccineCardDataBase
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import javax.inject.Inject

/**
 * [LocalDataSource]
 *
 * @author Pinakin Kansara
 */
class LocalDataSource @Inject constructor(
    private val dataBase: BcVaccineCardDataBase
) {

    suspend fun insert(card: HealthCard) = dataBase.getHealthCardDao().insert(card)

    fun getCards() = dataBase.getHealthCardDao().getCards()
}
