package ca.bc.gov.repository.worker

import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import ca.bc.gov.data.datasource.remote.MobileConfigRemoteDataSource
import ca.bc.gov.data.datasource.remote.model.response.MobileConfigurationResponse
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 16,May,2022
 */
class MobileConfigRepository @Inject constructor(
    private val mobileConfigRemoteDataSource: MobileConfigRemoteDataSource,
    private val encryptedPreferenceStorage: EncryptedPreferenceStorage
) {

    suspend fun getRemoteApiVersion(): Int {
        return fetchAndStoreMobileConfiguration().version
    }

    suspend fun refreshMobileConfiguration(): Boolean {
        return fetchAndStoreMobileConfiguration().online ?: false
    }

    private suspend fun fetchAndStoreMobileConfiguration(): MobileConfigurationResponse {
        val response = mobileConfigRemoteDataSource.getMobileConfiguration()
        updatePreferenceStorage(response)
        return response
    }

    private fun updatePreferenceStorage(response: MobileConfigurationResponse) {
        encryptedPreferenceStorage.apply {
            baseUrl = response.baseUrl
            baseUrlIsOnline = response.online ?: false
        }
    }
}
