package ca.bc.gov.preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import ca.bc.gov.common.R
import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.common.model.settings.AnalyticsFeature
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
        private const val ON_BOARDING_REQUIRED = "ON_BOARDING_SHOWN"
        private const val DEPENDENT_ON_BOARDING_REQUIRED = "DEPENDENT_ON_BOARDING_REQUIRED"
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
        private const val APP_VERSION_CODE = "APP_VERSION_CODE"
        private const val RE_ON_BOARDING_REQUIRED = "RE_ON_BOARDING_REQUIRED"
        private const val PREVIOUS_ON_BOARDING_SCREEN_NAME = "PREVIOUS_ON_BOARDING_SCREEN_NAME"
        private const val QUICK_ACCESS_TILE_MANAGEMENT_TUTORIAL = "QUICK_ACCESS_TILE_MANAGEMENT_TUTORIAL"
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

    val recentPhnDobData: Flow<String> = flow {
        val data = encryptedSharedPreferences.getString(RECENT_PHN_DOB, null)
        if (data != null) {
            emit(data)
        }
    }

    var onBoardingRequired: Boolean
        get() = encryptedSharedPreferences.getBoolean(ON_BOARDING_REQUIRED, true)
        set(value) {
            encryptedSharedPreferences.edit()
                .putBoolean(ON_BOARDING_REQUIRED, value)
                .apply()
        }

    var dependentOnBoardingRequired: Boolean
        get() = encryptedSharedPreferences.getBoolean(DEPENDENT_ON_BOARDING_REQUIRED, true)
        set(value) {
            encryptedSharedPreferences.edit()
                .putBoolean(DEPENDENT_ON_BOARDING_REQUIRED, value)
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

    var previousAppVersionCode: Int
        get() = encryptedSharedPreferences.getInt(APP_VERSION_CODE, 0)
        set(value) {
            encryptedSharedPreferences.edit().putInt(APP_VERSION_CODE, value)
                .apply()
        }

    var previousOnBoardingScreenName: String?
        get() = encryptedSharedPreferences.getString(PREVIOUS_ON_BOARDING_SCREEN_NAME, null)
        set(value) {
            encryptedSharedPreferences.edit().putString(PREVIOUS_ON_BOARDING_SCREEN_NAME, value)
                .apply()
        }

    var isReOnBoardingRequired: Boolean
        get() = encryptedSharedPreferences.getBoolean(RE_ON_BOARDING_REQUIRED, false)
        set(value) {
            encryptedSharedPreferences.edit().putBoolean(RE_ON_BOARDING_REQUIRED, value)
                .apply()
        }

    var isQuickAccessTileTutorialRequired: Boolean
        get() = encryptedSharedPreferences.getBoolean(QUICK_ACCESS_TILE_MANAGEMENT_TUTORIAL, true)
        set(value) {
            encryptedSharedPreferences.edit()
                .putBoolean(QUICK_ACCESS_TILE_MANAGEMENT_TUTORIAL, value)
                .apply()
        }

    suspend fun clearAuthState() {
        encryptedSharedPreferences.edit().remove(AUTH_STATE).apply()
    }
}
