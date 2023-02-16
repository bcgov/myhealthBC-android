package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationForecastEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface ImmunizationForecastDao : BaseDao<ImmunizationForecastEntity>
