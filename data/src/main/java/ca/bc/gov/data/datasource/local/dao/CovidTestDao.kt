package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.covid.CovidTestEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface CovidTestDao : BaseDao<CovidTestEntity> {

    @Query("SELECT * FROM covid_test WHERE order_id = :orderId")
    suspend fun findByCovidOrderId(orderId: Long): CovidTestEntity?

    @Query("DELETE FROM covid_test WHERE order_id = :orderId")
    suspend fun delete(orderId: Long): Int
}
