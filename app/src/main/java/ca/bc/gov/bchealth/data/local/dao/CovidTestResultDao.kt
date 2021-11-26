package ca.bc.gov.bchealth.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import kotlinx.coroutines.flow.Flow

/*
* Created by amit_metri on 26,November,2021
*/
@Dao
interface CovidTestResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(covidTestResult: CovidTestResult)

    @Delete
    suspend fun delete(covidTestResult: CovidTestResult)

    @Query("SELECT * FROM covid_test_results order by resultDateTime desc")
    fun getCovidTestResults(): Flow<List<CovidTestResult>>

}