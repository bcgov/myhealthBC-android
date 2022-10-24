package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.dependent.DependentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DependentDao : BaseDao<DependentEntity> {

    @Query(
        "SELECT *, " +
            "(SELECT id FROM patient WHERE authentication_status = 'AUTHENTICATED') AS patientId " +
            "FROM dependent WHERE patient_id = patientId"
    )
    fun findDependents(): Flow<List<DependentEntity>>

    @Query("DELETE FROM dependent")
    suspend fun deleteAll(): Int
}
