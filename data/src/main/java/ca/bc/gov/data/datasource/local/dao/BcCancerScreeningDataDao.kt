package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.services.BcCancerScreeningDataEntity

/**
 * @author pinakin.kansara
 * Created 2024-01-18 at 12:45 p.m.
 */
@Dao
interface BcCancerScreeningDataDao : BaseDao<BcCancerScreeningDataEntity> {

    @Query("SELECT * FROM bc_cancer_screening where id = :id")
    suspend fun getBcCancerScreeningDataDetails(id: Long): BcCancerScreeningDataEntity?
}
