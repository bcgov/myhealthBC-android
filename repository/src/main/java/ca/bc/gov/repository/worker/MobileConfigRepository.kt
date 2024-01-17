package ca.bc.gov.repository.worker

import ca.bc.gov.common.exceptions.ServiceDownException
import ca.bc.gov.data.datasource.remote.MobileConfigRemoteDataSource
import ca.bc.gov.data.datasource.remote.model.response.MobileConfigurationResponse
import ca.bc.gov.preference.EncryptedPreferenceStorage
import ca.bc.gov.repository.PreferenceRepository
import javax.inject.Inject

/**
 * @author: Created by Rashmi Bambhania on 16,May,2022
 */
class MobileConfigRepository @Inject constructor(
    private val mobileConfigRemoteDataSource: MobileConfigRemoteDataSource,
    private val encryptedPreferenceStorage: EncryptedPreferenceStorage,
    private val preferenceRepository: PreferenceRepository
) {

    @Throws(ServiceDownException::class)
    suspend fun getRemoteApiVersion(): Int {
        return fetchAndStoreMobileConfiguration().version
    }

    @Throws(ServiceDownException::class)
    suspend fun refreshMobileConfiguration() {
        fetchAndStoreMobileConfiguration()
    }

    private suspend fun fetchAndStoreMobileConfiguration(): MobileConfigurationResponse {
        val response = mobileConfigRemoteDataSource.getMobileConfiguration()
        updatePreferenceStorage(response)
        return response
    }

    private fun updatePreferenceStorage(response: MobileConfigurationResponse) {
        clearAuthStateIfFirstFetch()

        encryptedPreferenceStorage.apply {
            baseUrl = response.baseUrl
            authenticationEndpoint = response.authentication.endpoint
            clientId = response.authentication.clientId
            identityProviderId = response.authentication.identityProviderId
            baseUrlIsOnline = response.online ?: false
        }
    }

    private fun clearAuthStateIfFirstFetch() {
        if (encryptedPreferenceStorage.authenticationEndpoint == null) {
            encryptedPreferenceStorage.authState = null
            encryptedPreferenceStorage.sessionTime = -1L
        }
    }

    suspend fun syncConfig() {

        suspend fun updatePreference(response: MobileConfigurationResponse) {
            with(preferenceRepository) {

                if (getAuthenticationEndPoint().isNullOrBlank()) {
                    clearAuthState()
                }

                setClientId(response.authentication.clientId)
                setIdentityProviderId(response.authentication.identityProviderId)
                setAuthenticationEndpoint(response.authentication.endpoint)
                setBaseUrl(response.baseUrl)
                setBaseUrlOnline(response.online ?: false)
            }
        }

        val response = mobileConfigRemoteDataSource.getMobileConfiguration()
        updatePreference(response)
    }
}
