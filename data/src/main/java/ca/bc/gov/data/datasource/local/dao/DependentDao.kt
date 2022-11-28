package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ca.bc.gov.data.datasource.local.entity.dependent.DependentAndListOrder
import ca.bc.gov.data.datasource.local.entity.dependent.DependentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DependentDao : BaseDao<DependentEntity> {

    @Transaction
    @Query(
        "SELECT *, " +
            "(SELECT id FROM patient WHERE authentication_status = 'AUTHENTICATED') AS patientId " +
            "FROM dependent WHERE guardian_id = patientId"
    )
    fun findDependents(): Flow<List<DependentAndListOrder>>

    @Query(
        "SELECT *, " +
            "(SELECT id FROM patient WHERE authentication_status = 'AUTHENTICATED') AS patientId " +
            "FROM dependent WHERE patient_id = patientId AND phn = :phn"
    )
    suspend fun findDependent(phn: String): List<DependentEntity>

    @Query("SELECT * FROM dependent WHERE patient_id = :patientId")
    suspend fun findDependent(patientId: Long): DependentEntity?

    @Query("UPDATE dependent SET is_cache_valid = :isCacheValid WHERE patient_id = :patientId")
    suspend fun updateDependentCacheFlag(patientId: Long, isCacheValid: Boolean)

    @Query("DELETE FROM dependent where patient_id = :patientId")
    suspend fun deleteDependentById(patientId: Long): Int

    @Query("DELETE FROM dependent")
    suspend fun deleteAll(): Int
}
