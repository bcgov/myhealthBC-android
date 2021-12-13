package ca.bc.gov.bchealth.datasource

import ca.bc.gov.bchealth.data.local.BcVaccineCardDataBase
import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
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

    suspend fun update(card: HealthCard) = dataBase.getHealthCardDao().update(card)

    suspend fun unLink(card: HealthCard) = dataBase.getHealthCardDao().delete(card)

    suspend fun deleteVaccineData(healthPassId: Int) =
        dataBase.getHealthCardDao().deleteVaccineData(healthPassId)

    fun getCards() = dataBase.getHealthCardDao().getCards()

    suspend fun rearrange(cards: List<HealthCard>) = dataBase.getHealthCardDao().rearrange(cards)

    suspend fun insertCovidTests(covidTestResult: List<CovidTestResult>) =
        dataBase.getCovidTestResultDao().insertCovidTests(covidTestResult)

    suspend fun deleteCovidTestResult(reportId: String) =
        dataBase.getCovidTestResultDao().delete(reportId)

    fun getCovidTestResults() = dataBase.getCovidTestResultDao().getCovidTestResults()

    suspend fun deleteAllRecords() {
        dataBase.getHealthCardDao().deleteAllCards()
        dataBase.getCovidTestResultDao().deleteAllCovidTestResults()
    }
}
