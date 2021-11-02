package ca.bc.gov.bchealth.datasource

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import ca.bc.gov.bchealth.BuildConfig
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
        val HEALTH_RECORD_INTRO_SHOWN = booleanPreferencesKey("HEALTH_RECORD_INTRO_SHOWN")
    }

    val isOnBoardingShown: Flow<Boolean> = context.dataStore.data.map { preference ->
        preference[ON_BOARDING_SHOWN] ?: false
    }

    suspend fun setOnBoardingShown(shown: Boolean = true) = context.dataStore.edit { preference ->
        preference[ON_BOARDING_SHOWN] = shown
    }

    val isHealthRecordIntroShown: Flow<Boolean> = context.dataStore.data.map { preference ->
        preference[HEALTH_RECORD_INTRO_SHOWN] ?: false
    }

    suspend fun setHealthRecordIntroShown(shown: Boolean = true) = context.dataStore.edit { preference ->
        preference[HEALTH_RECORD_INTRO_SHOWN] = shown
    }
}
