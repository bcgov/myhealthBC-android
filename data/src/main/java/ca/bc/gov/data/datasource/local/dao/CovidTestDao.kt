package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import ca.bc.gov.data.datasource.local.entity.covid.CovidTestEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface CovidTestDao : BaseDao<CovidTestEntity>