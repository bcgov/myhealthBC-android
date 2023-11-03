package ca.bc.gov.repository

import ca.bc.gov.common.model.userprofile.UserProfileDto
import ca.bc.gov.data.datasource.local.UserProfileLocalDataSource
import ca.bc.gov.data.datasource.remote.UserProfileRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import javax.inject.Inject

/*
* Created by amit_metri on 18,March,2022
*/
class UserProfileRepository @Inject constructor(
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource
) {

    suspend fun getUserProfile(token: String, hdid: String, patientId: Long): UserProfileDto {
        val localUserProfile = userProfileLocalDataSource.getUserProfile(patientId)

        return if (localUserProfile == null) {
            val response = userProfileRemoteDataSource.getUserProfile(token, hdid)
            val dto = response.resourcePayload.toDto(patientId)
            userProfileLocalDataSource.insert(dto)
            dto
        } else {
            localUserProfile.toDto()
        }
    }

    suspend fun checkAgeLimit(token: String, hdid: String): Boolean {
        return userProfileRemoteDataSource.checkAgeLimit(token, hdid)
    }

    suspend fun checkForTermsOfServices(token: String, hdid: String): UserProfileDto {
        return userProfileRemoteDataSource.getUserProfile(token, hdid).resourcePayload.toDto(0)
    }

    suspend fun acceptTermsOfService(
        token: String,
        hdid: String,
        termsOfServiceId: String,
        hasTOSUpdated: Boolean = false
    ): Boolean {
        val response =
            userProfileRemoteDataSource.acceptTermsOfService(token, hdid, termsOfServiceId, hasTOSUpdated)

        return response.resourcePayload.acceptedTermsOfService && !response.resourcePayload.hasTermsOfServiceUpdated
    }

    suspend fun deleteUserProfileCache(patientId: Long) {
        userProfileLocalDataSource.deleteUserProfile(patientId)
    }
}
