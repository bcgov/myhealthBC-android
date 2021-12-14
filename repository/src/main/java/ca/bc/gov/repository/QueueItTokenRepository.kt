package ca.bc.gov.repository

import ca.bc.gov.data.local.preference.EncryptedPreferenceStorage
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class QueueItTokenRepository @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) {

    companion object {
        private const val TAG = "QueueItTokenRepository"
    }

    fun setQueItToken(token: String?) {
        preferenceStorage.queueItToken = token
    }
}