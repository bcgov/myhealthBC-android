package ca.bc.gov.data.utils

import ca.bc.gov.data.local.preference.EncryptedPreferenceStorage
import java.security.SecureRandom
import javax.inject.Inject

/*
* Created by amit_metri on 07,December,2021
*/
class RandomBytesGenerator @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) {

    fun getSecureRandom(): ByteArray {
        return if (preferenceStorage.passPhrase.isEmpty()) {
            val random = generateRandom()
            preferenceStorage.passPhrase = random
            random
        } else {
            preferenceStorage.passPhrase
        }
    }

    private fun generateRandom(): ByteArray {
        return SecureRandom().generateSeed(24)
    }
}
