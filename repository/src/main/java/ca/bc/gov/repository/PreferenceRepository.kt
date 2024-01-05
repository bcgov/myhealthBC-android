package ca.bc.gov.repository

import ca.bc.gov.preference.EncryptedPreferenceStorage
import javax.inject.Inject

/**
 * @author pinakin.kansara
 * Created 2023-12-13 at 3:14 p.m.
 */
class PreferenceRepository @Inject constructor(
    private val encryptedPreferenceStorage: EncryptedPreferenceStorage
) {

    suspend fun setClientId(clientId: String) {
        encryptedPreferenceStorage.clientId = clientId
    }

    suspend fun setBaseUrl(baseUrl: String) {
        encryptedPreferenceStorage.baseUrl = baseUrl
    }

    suspend fun setAuthenticationEndpoint(authEndpointUrl: String) {
        encryptedPreferenceStorage.authenticationEndpoint = authEndpointUrl
    }

    suspend fun getAuthenticationEndPoint(): String? = encryptedPreferenceStorage.authenticationEndpoint

    suspend fun setIdentityProviderId(identityProviderId: String) {
        encryptedPreferenceStorage.identityProviderId = identityProviderId
    }

    suspend fun setBaseUrlOnline(isBaseUrlOnline: Boolean) {
        encryptedPreferenceStorage.baseUrlIsOnline = isBaseUrlOnline
    }

    suspend fun clearAuthState() {
        encryptedPreferenceStorage.clearAuthState()
    }
}
