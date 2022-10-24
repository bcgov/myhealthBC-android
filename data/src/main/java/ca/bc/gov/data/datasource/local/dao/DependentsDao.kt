package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.dependent.DependentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DependentsDao : BaseDao<DependentEntity> {

    @Query("SELECT * FROM dependent")
    fun findDependents(): Flow<List<DependentEntity>>

    @Query("DELETE FROM dependent")
    suspend fun deleteAll(): Int
}
