package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.immunization.ImmunizationForecastDto
import ca.bc.gov.data.datasource.local.dao.ImmunizationForecastDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class ImmunizationForecastLocalDataSource @Inject constructor(
    private val immunizationForecastDao: ImmunizationForecastDao
) {

    suspend fun insert(immunizationForecast: ImmunizationForecastDto): Long {
        return immunizationForecastDao.insert(immunizationForecast.toEntity())
    }

    suspend fun insert(immunizationForecasts: List<ImmunizationForecastDto>): List<Long> =
        immunizationForecastDao.insert(immunizationForecasts.map { it.toEntity() })
}
