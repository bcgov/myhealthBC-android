package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.labtest.LabOrderDto
import ca.bc.gov.common.model.labtest.LabOrderWithLabTestsAndPatientDto
import ca.bc.gov.data.datasource.local.dao.LabOrderDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class LabOrderLocalDataSource @Inject constructor(
    private val labOrderDao: LabOrderDao
) {

    suspend fun insert(labOrder: LabOrderDto): Long {
        return labOrderDao.insert(labOrder.toEntity())
    }

    suspend fun insert(labOrders: List<LabOrderDto>): List<Long> {
        return labOrderDao.insert(labOrders.map { it.toEntity() })
    }

    suspend fun findByLabOrderId(labOrderId: Long): LabOrderWithLabTestsAndPatientDto? =
        labOrderDao.findByLabOrderId(labOrderId)?.toDto()

    suspend fun delete(patientId: Long): Int = labOrderDao.delete(patientId)
}
