package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.specialauthority.SpecialAuthorityEntity

/*
* Created by amit_metri on 27,June,2022
*/
@Dao
interface SpecialAuthorityDao : BaseDao<SpecialAuthorityEntity> {
    @Query("DELETE FROM special_authority WHERE patient_id = :patientId")
    suspend fun delete(patientId: Long): Int

    @Query("SELECT * FROM special_authority WHERE special_authority_id = :id")
    suspend fun getSpecialAuthorityDetails(id: Long): SpecialAuthorityEntity?
}
