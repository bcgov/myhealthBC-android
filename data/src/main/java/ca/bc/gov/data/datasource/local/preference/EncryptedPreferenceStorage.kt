package ca.bc.gov.data.datasource.local.preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.common.model.settings.AnalyticsFeature
import ca.bc.gov.data.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class EncryptedPreferenceStorage @Inject constructor(
    private val encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val ANALYTICS_FEATURE = "ANALYTICS_FEATURE"
        private const val QUEUE_IT_TOKEN = "QUEUE_IT_TOKEN"
        private const val APP_VERSION_CODE = "APP_VERSION_CODE"
        private const val ON_BOARDING_SHOWN = "ON_BOARDING_SHOWN"
        private const val RECENT_PHN_DOB = "RECENT_PHN_DOB"
        private const val COOKIE = "cookie"
        private const val PASS_PHRASE = "RECORD"
        private const val AUTH_STATE = "STATE"
        private const val PROTECTIVE_WORD = "PROTECTIVE_WORD"
        private const val PROTECTIVE_WORD_STATE = "PROTECTIVE_WORD_STATE"
        private const val POST_LOGIN_CHECK = "POST_LOGIN_CHECK"
        private const val SESSION_TIME = "SESSION_TIME"
        private const val BCSC_SHOWN_POST_BIOMETRIC = "BCSC_SHOWN_POST_BIOMETRIC"
        private const val BASE_URL = "BASE_URL"
        private const val AUTHENTICATION_ENDPOINT = "AUTHENTICATION_ENDPOINT"
        private const val CLIENT_ID = "CLIENT_ID"
        private const val IDENTITY_PROVIDER_ID = "IDENTITY_PROVIDER_ID"

        private const val BASE_URL_IS_ONLINE = "BASE_URL_IS_ONLINE"
    }

    var queueItToken: String?
        get() = encryptedSharedPreferences.getString(QUEUE_IT_TOKEN, null)
        set(value) {
            encryptedSharedPreferences.edit().putString(QUEUE_IT_TOKEN, value).apply()
        }

    var cookies: MutableSet<String>?
        get() = encryptedSharedPreferences.getStringSet(COOKIE, emptySet())
        set(value) {
            encryptedSharedPreferences.edit().putStringSet(COOKIE, value).apply()
        }

    var passPhrase: ByteArray
        get() =
            Base64.decode(
                encryptedSharedPreferences.getString(PASS_PHRASE, "") ?: "",
                Base64.DEFAULT
            )
        set(value) {
            encryptedSharedPreferences.edit().putString(
                PASS_PHRASE,
                Base64.encodeToString(value, Base64.DEFAULT)
            ).apply()
        }

    val analyticsFeature: Flow<AnalyticsFeature?> = flow {
        val value =
            encryptedSharedPreferences.getInt(ANALYTICS_FEATURE, 0)
        emit(AnalyticsFeature.getByValue(value))
    }

    val appVersion: Int = encryptedSharedPreferences.getInt(APP_VERSION_CODE, 0)

    val recentPhnDobData: Flow<String> = flow {
        val data = encryptedSharedPreferences.getString(RECENT_PHN_DOB, null)
        if (data != null) {
            emit(data)
        }
    }

    var onBoardingRequired: Boolean
        get() = encryptedSharedPreferences.getBoolean(ON_BOARDING_SHOWN, true)
        set(value) {
            encryptedSharedPreferences.edit()
                .putBoolean(ON_BOARDING_SHOWN, value)
                .apply()
        }

    var onBCSCLoginRequiredPostBiometric: Boolean
        get() = encryptedSharedPreferences.getBoolean(BCSC_SHOWN_POST_BIOMETRIC, true)
        set(value) {
            encryptedSharedPreferences.edit()
                .putBoolean(BCSC_SHOWN_POST_BIOMETRIC, value)
                .apply()
        }

    fun setAnalyticsFeature(feature: AnalyticsFeature) =
        encryptedSharedPreferences.edit()
            .putInt(ANALYTICS_FEATURE, feature.value)
            .apply()

    suspend fun setAppVersion(versionCode: Int) =
        encryptedSharedPreferences.edit()
            .putInt(APP_VERSION_CODE, versionCode)
            .commit()

    fun setRecentPhnDob(data: String) =
        encryptedSharedPreferences.edit().putString(RECENT_PHN_DOB, data).apply()

    fun clear() = encryptedSharedPreferences.edit().clear().apply()

    var authState: String?
        get() = encryptedSharedPreferences.getString(AUTH_STATE, null)
        set(value) {
            encryptedSharedPreferences.edit().putString(
                AUTH_STATE, value
            ).apply()
        }

    var protectiveWord: String?
        get() = encryptedSharedPreferences.getString(PROTECTIVE_WORD, null)
        set(value) {
            encryptedSharedPreferences.edit().putString(PROTECTIVE_WORD, value).apply()
        }

    var protectiveWordState: Int
        get() = encryptedSharedPreferences.getInt(
            PROTECTIVE_WORD_STATE,
            ProtectiveWordState.PROTECTIVE_WORD_NOT_REQUIRED.value
        )
        set(value) {
            encryptedSharedPreferences.edit().putInt(PROTECTIVE_WORD_STATE, value).apply()
        }

    var postLoginCheck: String?
        get() = encryptedSharedPreferences.getString(POST_LOGIN_CHECK, null)
        set(value) {
            encryptedSharedPreferences.edit().putString(POST_LOGIN_CHECK, value).apply()
        }

    var sessionTime: Long
        get() = encryptedSharedPreferences.getLong(SESSION_TIME, -1L)
        set(value) {
            encryptedSharedPreferences.edit().putLong(SESSION_TIME, value).apply()
        }

    var baseUrl: String?
        get() = encryptedSharedPreferences.getString(BASE_URL, context.getString(R.string.base_url))
            ?: context.getString(R.string.base_url)
        set(value) {
            encryptedSharedPreferences.edit().putString(BASE_URL, value).apply()
        }

    var authenticationEndpoint: String?
        get() = encryptedSharedPreferences.getString(AUTHENTICATION_ENDPOINT, null)
        set(value) {
            encryptedSharedPreferences.edit().putString(AUTHENTICATION_ENDPOINT, value).apply()
        }

    var clientId: String?
        get() = encryptedSharedPreferences.getString(CLIENT_ID, null)
        set(value) {
            encryptedSharedPreferences.edit().putString(CLIENT_ID, value).apply()
        }

    var identityProviderId: String?
        get() = encryptedSharedPreferences.getString(IDENTITY_PROVIDER_ID, null)
        set(value) {
            encryptedSharedPreferences.edit().putString(IDENTITY_PROVIDER_ID, value).apply()
        }

    var baseUrlIsOnline: Boolean
        get() = encryptedSharedPreferences.getBoolean(BASE_URL_IS_ONLINE, false)
        set(value) {
            encryptedSharedPreferences.edit()
                .putBoolean(BASE_URL_IS_ONLINE, value)
                .apply()
        }
}
