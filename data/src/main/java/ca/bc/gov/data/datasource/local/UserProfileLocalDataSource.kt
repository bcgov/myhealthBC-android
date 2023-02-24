package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.userprofile.UserProfileDto
import ca.bc.gov.data.datasource.local.dao.UserProfileDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

class UserProfileLocalDataSource @Inject constructor(
    private val userProfileDao: UserProfileDao
) {
    suspend fun getUserProfile(patientId: Long) =
        userProfileDao.getUserProfile(patientId)

    suspend fun deleteUserProfile(patientId: Long) =
        userProfileDao.delete(patientId)

    suspend fun insert(userProfileDto: UserProfileDto) =
        userProfileDao.insert(userProfileDto.toEntity())
}
