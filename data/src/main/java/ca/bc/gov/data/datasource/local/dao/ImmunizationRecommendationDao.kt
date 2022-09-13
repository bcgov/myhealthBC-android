package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecommendationEntity

@Dao
interface ImmunizationRecommendationDao : BaseDao<ImmunizationRecommendationEntity> {

    @Query("SELECT * FROM immunization_recommendation WHERE recommendation_set_id = :id")
    suspend fun findByImmunizationRecommendationId(id: Long): ImmunizationRecommendationEntity

    @Query("DELETE FROM immunization_recommendation WHERE recommendation_set_id = :id")
    suspend fun delete(id: Long): Int
}
