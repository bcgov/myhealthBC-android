package ca.bc.gov.repository.testrecord

import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.data.datasource.local.CovidTestLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class CovidTestRepository @Inject constructor(
    private val covidTestLocalDataSource: CovidTestLocalDataSource
) {
    suspend fun insert(covidTests: List<CovidTestDto>, orderId : Long): List<Long> =
        covidTestLocalDataSource.insert(covidTests, orderId)
}
