package ca.bc.gov.repository.worker

import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.datasource.remote.MobileConfigRemoteDataSource
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 16,May,2022
 */
class MobileConfigRepository @Inject constructor(
    private val mobileConfigRemoteDataSource: MobileConfigRemoteDataSource,
    private val encryptedPreferenceStorage: EncryptedPreferenceStorage
) {

    suspend fun getRemoteApiVersion(): Int =
        mobileConfigRemoteDataSource.getMobileConfiguration().version

    suspend fun getBaseUrl(): Boolean {
        val response = mobileConfigRemoteDataSource.getMobileConfiguration()
        encryptedPreferenceStorage.baseUrl = response.baseUrl
        encryptedPreferenceStorage.baseUrlIsOnline = response.online ?: false
        return response.online ?: false
    }
}
