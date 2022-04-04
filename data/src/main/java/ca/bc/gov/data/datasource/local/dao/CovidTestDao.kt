package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.covid.CovidTestEntity

/**
 * @author Pinakin Kansara
 */
@Dao
interface CovidTestDao : BaseDao<CovidTestEntity> {

    @Query("SELECT * FROM covid_test WHERE covid_order_id = :covidOrderId")
    suspend fun findByCovidOrderId(covidOrderId: String): CovidTestEntity?

    @Query("DELETE FROM covid_test WHERE covid_order_id = :covidOrderId")
    suspend fun delete(covidOrderId: String): Int
}
