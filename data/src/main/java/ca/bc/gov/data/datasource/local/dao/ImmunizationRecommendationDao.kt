package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecommendationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImmunizationRecommendationDao : BaseDao<ImmunizationRecommendationEntity> {

    @Query(
        "SELECT *, " +
            "(SELECT id FROM patient WHERE authentication_status = 'AUTHENTICATED') AS patientId " +
            "FROM immunization_recommendation WHERE patient_id = patientId ORDER BY agentDueDate DESC"
    )
    fun selectRecommendations(): Flow<List<ImmunizationRecommendationEntity>>

    @Query("SELECT EXISTS(SELECT * ,(SELECT id FROM patient WHERE authentication_status = 'AUTHENTICATED') AS patientId FROM immunization_recommendation)")
    fun hasRecommendations(): Boolean

    @Query("DELETE FROM immunization_recommendation WHERE recommendation_set_id = :id")
    suspend fun delete(id: Long): Int
}
