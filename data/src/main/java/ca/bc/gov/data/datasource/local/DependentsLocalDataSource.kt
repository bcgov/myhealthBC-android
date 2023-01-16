package ca.bc.gov.data.datasource.local

import ca.bc.gov.data.datasource.local.dao.DependentDao
import ca.bc.gov.data.datasource.local.dao.DependentListOrderDao
import ca.bc.gov.data.datasource.local.dao.PatientDao
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.dependent.DependentEntity
import ca.bc.gov.data.datasource.local.entity.dependent.DependentListOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DependentsLocalDataSource @Inject constructor(
    private val dependentsDao: DependentDao,
    private val patientDao: PatientDao,
    private val dependentListOrderDao: DependentListOrderDao,
) {

    fun getAllDependents(): Flow<List<DependentEntity>> =
        dependentsDao.findDependents().map { list ->
            list.sortedBy {
                it.listOrder?.order ?: Int.MAX_VALUE
            }.map { it.dependent }
        }

    suspend fun clearTables() {
        patientDao.deleteDependentPatients()
        dependentsDao.deleteAll()
    }

    suspend fun insertPatient(patientEntity: PatientEntity) =
        patientDao.insert(patientEntity)

    suspend fun insertDependent(dependentEntity: DependentEntity) {
        dependentsDao.insert(dependentEntity)
        insertDependentListOrder(dependentEntity.hdid, Int.MAX_VALUE)
    }

    suspend fun findDependent(patientId: Long) =
        dependentsDao.findDependent(patientId)

    suspend fun findDependentByPhn(phn: String): DependentEntity? =
        dependentsDao.findDependentByPhn(phn)

    suspend fun isDependentCacheValid(patientId: Long) =
        dependentsDao.findDependent(patientId)?.isCacheValid ?: false

    suspend fun enableDependentCacheFlag(patientId: Long) {
        dependentsDao.updateDependentCacheFlag(patientId, true)
    }

    suspend fun getDependentHdidOrNull(patientId: Long): String? =
        dependentsDao.findDependent(patientId)?.hdid

    suspend fun deleteAllDependentListOrders() {
        dependentListOrderDao.deleteAll()
    }

    suspend fun deleteDependent(patientId: Long) {
        dependentsDao.deleteDependentById(patientId)
        patientDao.deletePatientById(patientId)
    }

    suspend fun deleteDependentListOrdersExcept(dependentIds: List<String>) {
        dependentListOrderDao.deleteExcept(dependentIds)
    }

    suspend fun insertDependentListOrder(hdid: String, order: Int) {
        dependentListOrderDao.insert(DependentListOrder(hdid, order))
    }
}
