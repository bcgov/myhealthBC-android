package ca.bc.gov.repository

import ca.bc.gov.common.BuildConfig
import ca.bc.gov.preference.EncryptedPreferenceStorage
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class OnBoardingRepository @Inject constructor(
    private val preferenceStorage: EncryptedPreferenceStorage
) {

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

    var previousVersionCode: Int
        get() = preferenceStorage.previousAppVersionCode
        set(value) {
            preferenceStorage.previousAppVersionCode = value
        }

    var isReOnBoardingRequired: Boolean
        get() = preferenceStorage.isReOnBoardingRequired
        set(value) {
            preferenceStorage.isReOnBoardingRequired = value
        }

    var previousOnBoardingScreenName: String?
        get() = preferenceStorage.previousOnBoardingScreenName
        set(value) {
            preferenceStorage.previousOnBoardingScreenName = value
        }

    fun checkIfReOnBoardingRequired(currentAppVersionCode: Int) {
        if (!onBoardingRequired && currentAppVersionCode > previousVersionCode &&
            !BuildConfig.FLAG_NEW_ON_BOARDING_SCREEN.equals(
                    previousOnBoardingScreenName,
                    ignoreCase = true
                )
        ) {
            isReOnBoardingRequired = true
        }
    }

    fun checkIfAppUpdated(currentAppVersionCode: Int): Boolean {
        return currentAppVersionCode > previousVersionCode
    }
}
