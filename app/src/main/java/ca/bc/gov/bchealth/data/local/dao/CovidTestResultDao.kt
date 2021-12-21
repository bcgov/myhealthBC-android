package ca.bc.gov.bchealth.data.local.dao

import androidx.room.Dao
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
    suspend fun insertCovidTests(covidTestResults: List<CovidTestResult>)

    @Query("DELETE FROM covid_test_results WHERE combinedReportId = :combinedReportId")
    suspend fun delete(combinedReportId: String)

    @Query("SELECT * FROM covid_test_results order by resultDateTime desc")
    fun getCovidTestResults(): Flow<List<CovidTestResult>>

    @Query(
        "SELECT count(*) FROM covid_test_results" +
            " WHERE combinedReportId = :combinedReportId AND testStatus != 'Pending' "
    )
    suspend fun getMatchingCovidTestResultsCount(combinedReportId: String): Int

    @Query("DELETE from covid_test_results")
    suspend fun deleteAllCovidTestResults(): Int
}
