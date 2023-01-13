package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.hospitalvisit.HospitalVisitEntity

@Dao
interface HospitalVisitDao : BaseDao<HospitalVisitEntity> {

    @Query("DELETE FROM hospital_visit WHERE patient_id = :patientId")
    suspend fun delete(patientId: Long): Int

    @Query("SELECT * FROM hospital_visit WHERE hospital_visit_id = :id")
    suspend fun getHospitalVisitDetails(id: Long): HospitalVisitEntity?
}
