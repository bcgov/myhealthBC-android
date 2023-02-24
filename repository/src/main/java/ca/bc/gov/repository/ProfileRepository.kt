package ca.bc.gov.repository

import ca.bc.gov.common.model.userprofile.UserProfileDto
import ca.bc.gov.data.datasource.local.UserProfileLocalDataSource
import ca.bc.gov.data.datasource.remote.ProfileRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import javax.inject.Inject

/*
* Created by amit_metri on 18,March,2022
*/
class ProfileRepository @Inject constructor(
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val profileRemoteDataSource: ProfileRemoteDataSource
) {

    suspend fun getUserProfile(token: String, hdid: String, patientId: Long): UserProfileDto {
        val localUserProfile = userProfileLocalDataSource.getUserProfile(patientId)

        return if (localUserProfile == null) {
            val response = profileRemoteDataSource.getUserProfile(token, hdid)
            val dto = response.resourcePayload.toDto(patientId)
            userProfileLocalDataSource.insert(dto)
            dto
        } else {
            localUserProfile.toDto()
        }
    }

    suspend fun checkAgeLimit(token: String, hdid: String): Boolean {
        return profileRemoteDataSource.checkAgeLimit(token, hdid)
    }

    suspend fun isTermsOfServiceAccepted(token: String, hdid: String): Boolean {
        val response = profileRemoteDataSource.getUserProfile(token, hdid)
        return response.resourcePayload.acceptedTermsOfService
    }

    suspend fun acceptTermsOfService(
        token: String,
        hdid: String,
        termsOfServiceId: String,
    ): Boolean {
        val response = profileRemoteDataSource.acceptTermsOfService(token, hdid, termsOfServiceId)
        return response.resourcePayload.acceptedTermsOfService
    }

    suspend fun deleteUseProfileCache(patientId: Long) {
        userProfileLocalDataSource.deleteUserProfile(patientId)
    }
}
