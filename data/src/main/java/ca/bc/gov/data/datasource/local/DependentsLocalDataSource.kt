package ca.bc.gov.data.datasource.local

import ca.bc.gov.data.datasource.local.dao.DependentDao
import ca.bc.gov.data.datasource.local.entity.dependent.DependentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DependentsLocalDataSource @Inject constructor(
    private val dependentsDao: DependentDao
) {

    fun getAllDependents(): Flow<List<DependentEntity>> = dependentsDao.findDependents()

    suspend fun clearTable() =
        dependentsDao.deleteAll()

    suspend fun insertDependents(list: List<DependentEntity>) =
        dependentsDao.insert(list)
}
