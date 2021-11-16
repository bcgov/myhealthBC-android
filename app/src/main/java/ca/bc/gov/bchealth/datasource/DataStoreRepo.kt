package ca.bc.gov.bchealth.datasource

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ca.bc.gov.bchealth.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [DataStoreRepo]
 *
 * @author amit metri
 */
private val Context.dataStore by preferencesDataStore(BuildConfig.APPLICATION_ID + "_preferences")

class DataStoreRepo @Inject constructor(
    private val context: Context
) {

    companion object {
        val ON_BOARDING_SHOWN = booleanPreferencesKey("ON_BOARDING_SHOWN")
        val IS_ANALYTICS_ENABLED = booleanPreferencesKey("IS_ANALYTICS_ENABLED")
        /*
        * Below preference is required to show new feature screen to existing users.
        * This feature will be enabled from v1.0.4
        * If we want to show new feature screen to existing users,
        * we need to change key for NEW_FEATURE so that new storage with false flag is created
        * for existing users. Ex: Change NEW_FEATURE to NEW_FEATURE_FEDERAL_PASS
        * */
        val NEW_FEATURE = booleanPreferencesKey("NEW_FEATURE")
        val RECENT_FORM_DATA = stringPreferencesKey("RECENT_FORM_DATA")
    }

    val isOnBoardingShown: Flow<Boolean> = context.dataStore.data.map { preference ->
        preference[ON_BOARDING_SHOWN] ?: false
    }

    suspend fun setOnBoardingShown(shown: Boolean = true) = context.dataStore.edit { preference ->
        preference[ON_BOARDING_SHOWN] = shown
    }

    val isAnalyticsEnabled: Flow<Boolean> = context.dataStore.data.map { preference ->
        preference[IS_ANALYTICS_ENABLED] ?: false
    }

    suspend fun trackAnalytics(shown: Boolean = true) = context.dataStore.edit { preference ->
        preference[IS_ANALYTICS_ENABLED] = shown
    }

    val isNewFeatureShown: Flow<Boolean> = context.dataStore.data.map { preference ->
        preference[NEW_FEATURE] ?: false
    }

    suspend fun setNewFeatureShown(shown: Boolean = true) = context.dataStore.edit { preference ->
        preference[NEW_FEATURE] = shown
    }

    val isRecentFormData: Flow<String> = context.dataStore.data.map { preference ->
        preference[RECENT_FORM_DATA] ?: ""
    }

    suspend fun setRecentFormData(formData: String) = context.dataStore.edit { preference ->
        preference[RECENT_FORM_DATA] = formData
    }
}
