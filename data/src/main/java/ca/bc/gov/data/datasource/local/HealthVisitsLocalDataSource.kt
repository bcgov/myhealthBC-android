package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.data.datasource.local.dao.HealthVisitsDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/*
* Created by amit_metri on 21,June,2022
*/
class HealthVisitsLocalDataSource @Inject constructor(
    private val healthVisitsDao: HealthVisitsDao
) {
    suspend fun deleteHealthVisits(patientId: Long) = healthVisitsDao.delete(patientId)

    suspend fun insert(healthVisits: List<HealthVisitsDto>) = healthVisitsDao.insert(
        healthVisits.map {
            it.toEntity()
        }
    )
}
