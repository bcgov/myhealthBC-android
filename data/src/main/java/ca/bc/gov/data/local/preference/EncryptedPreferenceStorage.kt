package ca.bc.gov.data.local.preference

import android.content.SharedPreferences
import android.util.Base64
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
        private const val RECENT_PHN_DOB = "RECENT_PHN_DOB"
        private const val COOKIE = "cookie"
        private const val PASS_PHRASE = "RECORD"
    }

    var queueItToken: String?
        get() = encryptedSharedPreferences.getString(QUEUE_IT_TOKEN, null)
        set(value) {
            encryptedSharedPreferences.edit().putString(QUEUE_IT_TOKEN, value).apply()
        }

    var cookies: MutableSet<String>?
    get() = encryptedSharedPreferences.getStringSet(COOKIE, emptySet())
    set(value){
        encryptedSharedPreferences.edit().putStringSet(COOKIE,value).apply()
    }

    var passPhrase: ByteArray
        get() =
            Base64.decode(encryptedSharedPreferences.getString(PASS_PHRASE, "") ?: "", Base64.DEFAULT)
        set(value) {
            encryptedSharedPreferences.edit().putString(
                PASS_PHRASE,
                Base64.encodeToString(value, Base64.DEFAULT)
            ).apply()
        }

    val analyticsFeature: Flow<AnalyticsFeature> = flow {
        val value =
            encryptedSharedPreferences.getInt(ANALYTICS_FEATURE, AnalyticsFeature.DISABLED.value)
        emit(AnalyticsFeature.getByValue(value) ?: AnalyticsFeature.DISABLED)
    }

    val appVersion: Int = encryptedSharedPreferences.getInt(APP_VERSION_CODE, 0)

    val recentPhnDobData: Flow<String> = flow {
        val data = encryptedSharedPreferences.getString(RECENT_PHN_DOB, null)
        if (data != null) {
            emit(data)
        }
    }

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

    fun setRecentPhnDob(data: String) =
        encryptedSharedPreferences.edit().putString(RECENT_PHN_DOB, data).apply()
}
