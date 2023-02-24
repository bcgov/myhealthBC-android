package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.userprofile.UserProfileEntity

@Dao
interface UserProfileDao : BaseDao<UserProfileEntity> {
    @Query("DELETE FROM user_profile WHERE patient_id = :patientId")
    suspend fun delete(patientId: Long): Int

    @Query("SELECT * FROM user_profile WHERE patient_id = :patientId")
    suspend fun getUserProfile(patientId: Long): UserProfileEntity?
}
