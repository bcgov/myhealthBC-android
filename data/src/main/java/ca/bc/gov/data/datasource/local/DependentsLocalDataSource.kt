package ca.bc.gov.data.datasource.local

import ca.bc.gov.data.datasource.local.dao.DependentDao
import ca.bc.gov.data.datasource.local.dao.PatientDao
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.dependent.DependentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DependentsLocalDataSource @Inject constructor(
    private val dependentsDao: DependentDao,
    private val patientDao: PatientDao,
) {

    fun getAllDependents(): Flow<List<DependentEntity>> = dependentsDao.findDependents()

    suspend fun clearTables() {
        patientDao.deleteDependentPatients()
        dependentsDao.deleteAll()
    }

    suspend fun insertPatient(patientEntity: PatientEntity) =
        patientDao.insert(patientEntity)

    suspend fun insertDependent(dependentEntity: DependentEntity) =
        dependentsDao.insert(dependentEntity)

    suspend fun findDependent(phn: String) =
        dependentsDao.findDependent(phn)
}
