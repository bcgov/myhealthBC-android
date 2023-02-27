package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderWithCovidTestsAndPatient

/**
 * @author Pinakin Kansara
 */
@Dao
interface CovidOrderDao : BaseDao<CovidOrderEntity> {
    @Query("SELECT * FROM covid_order WHERE id = :id")
    suspend fun findByCovidOrderId(id: Long): CovidOrderWithCovidTestsAndPatient?

    @Query("DELETE FROM covid_order WHERE patient_id = :patientId")
    suspend fun deleteByPatientId(patientId: Long): Int

    @Query("DELETE FROM covid_order WHERE id = :id")
    suspend fun delete(id: String): Int
}
