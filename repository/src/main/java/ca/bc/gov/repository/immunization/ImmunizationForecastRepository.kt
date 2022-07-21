package ca.bc.gov.repository.immunization

import ca.bc.gov.common.model.immunization.ImmunizationForecastDto
import ca.bc.gov.data.datasource.local.ImmunizationForecastLocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class ImmunizationForecastRepository @Inject constructor(
    private val immunizationForecastLocalDataSource: ImmunizationForecastLocalDataSource
) {

    suspend fun insert(immunizationForecast: ImmunizationForecastDto): Long =
        immunizationForecastLocalDataSource.insert(immunizationForecast)

    suspend fun insert(immunizationForecasts: List<ImmunizationForecastDto>): List<Long> =
        immunizationForecastLocalDataSource.insert(immunizationForecasts)
}
