package ca.bc.gov.repository.healthvisits

import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.data.datasource.remote.HealthVisitsRemoteDataSource
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 20,June,2022
 */
class HealthVisitsRepository @Inject constructor(
    private val healthVisitsRemoteDataSource: HealthVisitsRemoteDataSource
) {

    suspend fun getHealthVisits(token: String, hdid: String): List<HealthVisitsDto> {
        return healthVisitsRemoteDataSource.getHealthVisits(token, hdid)
    }
}
