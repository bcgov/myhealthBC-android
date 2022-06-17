package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.test.CovidOrderDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestAndPatientDto
import ca.bc.gov.data.datasource.local.dao.CovidOrderDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class CovidOrderLocalDataSource @Inject constructor(
    private val covidOrderDao: CovidOrderDao
) {

    suspend fun insert(covidOrder: CovidOrderDto): Long {
        return covidOrderDao.insert(covidOrder.toEntity())
    }

    suspend fun insert(covidOrders: List<CovidOrderDto>): List<Long> {
        return covidOrderDao.insert(covidOrders.map { it.toEntity() })
    }

    suspend fun findCovidOrderById(covidOrderId: String): CovidOrderWithCovidTestAndPatientDto? =
        covidOrderDao.findByCovidOrderId(covidOrderId)?.toDto()

    suspend fun deleteByPatientId(patientId: Long): Int = covidOrderDao.deleteByPatientId(patientId)

    suspend fun delete(id: String): Int = covidOrderDao.delete(id)
}
