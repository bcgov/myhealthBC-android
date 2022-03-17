package ca.bc.gov.repository

import ca.bc.gov.data.datasource.remote.ProfileRemoteDataSource
import javax.inject.Inject

/*
* Created by amit_metri on 18,March,2022
*/
class ProfileRepository @Inject constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource
) {
    suspend fun checkAgeLimit(token: String, hdid: String): Boolean {
        return profileRemoteDataSource.checkAgeLimit(token, hdid)
    }
}
