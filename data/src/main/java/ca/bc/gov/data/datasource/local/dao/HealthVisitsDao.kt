package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.healthvisits.HealthVisitEntity

/*
* Created by amit_metri on 21,June,2022
*/
@Dao
interface HealthVisitsDao : BaseDao<HealthVisitEntity> {

    @Query("DELETE FROM health_visits WHERE patient_id = :patientId")
    suspend fun delete(patientId: Long): Int

    @Query("SELECT * FROM health_visits WHERE health_visit_id = :id")
    suspend fun getHealthVisitDetails(id: Long): HealthVisitEntity?
}
