package ca.bc.gov.repository

import ca.bc.gov.data.local.preference.EncryptedPreferenceStorage
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class OnBoardingRepository @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) {

    suspend fun setAppVersionCode(appVersionCode: Int) =
        preferenceStorage.setAppVersion(appVersionCode)

    suspend fun setOnBoardingRequired(onBoardingShown: Boolean) =
        preferenceStorage.setIsOnBoardingShown(onBoardingShown)

    val onBoardingRequired = preferenceStorage.onBoardingRequired
}