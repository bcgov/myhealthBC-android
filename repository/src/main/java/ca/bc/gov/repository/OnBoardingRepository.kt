package ca.bc.gov.repository

import ca.bc.gov.common.BuildConfig
import ca.bc.gov.data.datasource.local.preference.EncryptedPreferenceStorage
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

    var dependentOnBoardingRequired: Boolean
        get() = preferenceStorage.dependentOnBoardingRequired
        set(value) {
            preferenceStorage.dependentOnBoardingRequired = value
        }

    var onBCSCLoginRequiredPostBiometric: Boolean
        get() = preferenceStorage.onBCSCLoginRequiredPostBiometric
        set(value) {
            preferenceStorage.onBCSCLoginRequiredPostBiometric = value
        }

    var versionCode: Int
        get() = preferenceStorage.versionCode
        set(value) {
            preferenceStorage.versionCode = value
        }

    var reOnBoardingRequired: Boolean
        get() = preferenceStorage.reOnBoardingRequired
        set(value) {
            preferenceStorage.reOnBoardingRequired = value
        }

    fun setReOnBoardingRequiredFlag(currentAppVersionCode: Int) {
        if (!onBoardingRequired && currentAppVersionCode > versionCode && BuildConfig.FLAG_RE_ONBOARDING_REQUIRED) {
            versionCode = currentAppVersionCode
            reOnBoardingRequired = true
        }
    }
}
