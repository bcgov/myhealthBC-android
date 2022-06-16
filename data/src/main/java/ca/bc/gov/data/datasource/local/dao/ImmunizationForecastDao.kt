package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationForecastEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface ImmunizationForecastDao : BaseDao<ImmunizationForecastEntity> {

    @Query("SELECT * FROM immunization_forecast WHERE immunization_record_id = :id")
    suspend fun findByImmunizationRecordId(id: Long): ImmunizationForecastEntity

    @Query("DELETE FROM immunization_forecast WHERE immunization_record_id = :id")
    suspend fun delete(id: Long): Int
}
