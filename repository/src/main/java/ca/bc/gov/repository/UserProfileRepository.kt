package ca.bc.gov.repository

import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.userprofile.UserProfileDto
import ca.bc.gov.data.datasource.local.UserProfileLocalDataSource
import ca.bc.gov.data.datasource.remote.UserProfileRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.repository.settings.AppFeatureRepository
import javax.inject.Inject

/*
* Created by amit_metri on 18,March,2022
*/
class UserProfileRepository @Inject constructor(
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource,
    private val appFeatureRepository: AppFeatureRepository,
) {

    suspend fun refreshUserProfile(patientId: Long, authParameters: AuthParametersDto) {
        val response = userProfileRemoteDataSource.getUserProfile(
            authParameters.token,
            authParameters.hdid
        )

        val userProfileDto = response.resourcePayload.toDto(patientId)
        val quickLinksDto = response.resourcePayload.preferences?.quickLinks?.list?.map { it.toDto() }

        appFeatureRepository.updateQuickLinks(quickLinksDto)

        userProfileLocalDataSource.deleteUserProfile(patientId)
        userProfileLocalDataSource.insert(userProfileDto)
    }

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

    suspend fun isTermsOfServiceAccepted(token: String, hdid: String): Boolean {
        val response = userProfileRemoteDataSource.getUserProfile(token, hdid)
        return response.resourcePayload.acceptedTermsOfService
    }

    suspend fun acceptTermsOfService(
        token: String,
        hdid: String,
        termsOfServiceId: String,
    ): Boolean {
        return userProfileRemoteDataSource.acceptTermsOfService(token, hdid, termsOfServiceId)
    }
}
