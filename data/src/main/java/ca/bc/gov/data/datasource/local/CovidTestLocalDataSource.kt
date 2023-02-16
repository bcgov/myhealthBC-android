package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.data.datasource.local.dao.CovidTestDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

class CovidTestLocalDataSource @Inject constructor(
    private val covidTestDao: CovidTestDao
) {
    suspend fun insert(covidTests: List<CovidTestDto>, orderId: Long): List<Long> =
        covidTestDao.insert(covidTests.map { it.toEntity(orderId) })
}
