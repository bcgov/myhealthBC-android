package ca.bc.gov.bchealth.utils

import ca.bc.gov.bchealth.datasource.DataStoreRepo
import java.security.SecureRandom
import javax.inject.Inject

/*
* Created by amit_metri on 07,December,2021
*/
class RandomBytesGenerator @Inject constructor(
    private val dataStoreRepo: DataStoreRepo
) {

    fun getSecureRandom(): ByteArray {
        return if (dataStoreRepo.passPhrase.isEmpty()) {
            val random = generateRandom()
            dataStoreRepo.passPhrase = random
            random
        } else {
            dataStoreRepo.passPhrase
        }
    }

    private fun generateRandom(): ByteArray {
        return SecureRandom().generateSeed(24)
    }
}
