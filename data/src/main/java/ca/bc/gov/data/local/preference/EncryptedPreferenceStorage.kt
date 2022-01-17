package ca.bc.gov.data.local.preference

import android.content.SharedPreferences
import ca.bc.gov.common.model.settings.AnalyticsFeature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class EncryptedPreferenceStorage @Inject constructor(
    private val encryptedSharedPreferences: SharedPreferences
) {

    companion object {
        private const val ANALYTICS_FEATURE = "ANALYTICS_FEATURE"
        private const val QUEUE_IT_TOKEN = "QUEUE_IT_TOKEN"
        private const val APP_VERSION_CODE = "APP_VERSION_CODE"
        private const val ON_BOARDING_SHOWN = "ON_BOARDING_SHOWN"
    }

    var queueItToken: String?
        get() = encryptedSharedPreferences.getString(QUEUE_IT_TOKEN, null)
        set(value) {
            encryptedSharedPreferences.edit().putString(QUEUE_IT_TOKEN, value).apply()
        }

    val analyticsFeature: Flow<AnalyticsFeature> = flow {
        val value =
            encryptedSharedPreferences.getInt(ANALYTICS_FEATURE, AnalyticsFeature.DISABLED.value)
        emit(AnalyticsFeature.getByValue(value) ?: AnalyticsFeature.DISABLED)
    }

    val appVersion: Int = encryptedSharedPreferences.getInt(APP_VERSION_CODE, 0)

    val onBoardingRequired: Flow<Boolean> =
        flow { emit(encryptedSharedPreferences.getBoolean(ON_BOARDING_SHOWN, true)) }

    fun setAnalyticsFeature(feature: AnalyticsFeature) =
        encryptedSharedPreferences.edit()
            .putInt(ANALYTICS_FEATURE, feature.value)
            .apply()

    fun setIsOnBoardingShown(onBoardingShown: Boolean) =
        encryptedSharedPreferences.edit()
            .putBoolean(ON_BOARDING_SHOWN, onBoardingShown)
            .apply()

    fun setAppVersion(versionCode: Int) =
        encryptedSharedPreferences.edit()
            .putInt(APP_VERSION_CODE, versionCode)
            .apply()
}