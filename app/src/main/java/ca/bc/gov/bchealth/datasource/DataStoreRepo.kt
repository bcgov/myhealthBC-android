package ca.bc.gov.bchealth.datasource

import android.content.SharedPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * [DataStoreRepo]
 *
 * @author amit metri
 */
class DataStoreRepo @Inject constructor(
    private val encryptedPreferences: SharedPreferences
) {

    companion object {
        const val ON_BOARDING_SHOWN = "ON_BOARDING_SHOWN"
        const val IS_ANALYTICS_ENABLED = "IS_ANALYTICS_ENABLED"

        /*
        * Below preference is required to show new feature screen to existing users.
        * This feature will be enabled from v1.0.4
        * If we want to show new feature screen to existing users,
        * we need to change key for NEW_FEATURE so that new storage with false flag is created
        * for existing users. Ex: Change NEW_FEATURE to NEW_FEATURE_FEDERAL_PASS
        * */
        const val NEW_FEATURE = "NEW_FEATURE"
        const val RECENT_FORM_DATA = "RECENT_FORM_DATA"
    }

    val isOnBoardingShown: Flow<Boolean> = flow {
        emit(encryptedPreferences.getBoolean(ON_BOARDING_SHOWN, false))
    }

    fun setOnBoardingShown(shown: Boolean = true) {
        encryptedPreferences.edit().putBoolean(
            ON_BOARDING_SHOWN, shown
        ).apply()
    }

    val isAnalyticsEnabled: Flow<Boolean> = flow {
        emit(encryptedPreferences.getBoolean(IS_ANALYTICS_ENABLED, true))
    }

    fun trackAnalytics(shown: Boolean = true) {
        encryptedPreferences.edit().putBoolean(
            IS_ANALYTICS_ENABLED, shown
        ).apply()
    }

    val isNewFeatureShown: Flow<Boolean> = flow {
        emit(
            encryptedPreferences.getBoolean(NEW_FEATURE, false)
        )
    }

    fun setNewFeatureShown(shown: Boolean = true) {
        encryptedPreferences.edit().putBoolean(
            NEW_FEATURE, shown
        ).apply()
    }

    val isRecentFormData: Flow<String> = flow {
        emit(encryptedPreferences.getString(RECENT_FORM_DATA, "") ?: "")
    }

    fun setRecentFormData(formData: String) {
        encryptedPreferences.edit().putString(
            RECENT_FORM_DATA, formData
        ).apply()
    }
}
