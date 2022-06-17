package ca.bc.gov.repository

import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class OnBoardingRepository @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) {

    suspend fun setAppVersionCode(appVersionCode: Int) =
        preferenceStorage.setAppVersion(appVersionCode)

    var onBoardingRequired: Boolean
        get() = preferenceStorage.onBoardingRequired
        set(value) {
            preferenceStorage.onBoardingRequired = value
        }

    var onBCSCLoginRequiredPostBiometric: Boolean
        get() = preferenceStorage.onBCSCLoginRequiredPostBiometric
        set(value) {
            preferenceStorage.onBCSCLoginRequiredPostBiometric = value
        }
}
