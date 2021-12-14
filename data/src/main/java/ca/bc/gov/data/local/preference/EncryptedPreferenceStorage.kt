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

    suspend fun setAnalyticsFeature(feature: AnalyticsFeature) =
        encryptedSharedPreferences.edit()
            .putInt(ANALYTICS_FEATURE, feature.value)
            .apply()
}