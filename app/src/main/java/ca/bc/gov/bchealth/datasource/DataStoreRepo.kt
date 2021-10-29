package ca.bc.gov.bchealth.datasource

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        val ON_BOARDING_FRAGMENT_COUNTER = intPreferencesKey("ON_BOARDING_FRAGMENT_COUNTER")
    }

    val isOnBoardingShown: Flow<Boolean> = context.dataStore.data.map { preference ->
        preference[ON_BOARDING_SHOWN] ?: false
    }

    suspend fun setOnBoardingShown(shown: Boolean = true) = context.dataStore.edit { preference ->
        preference[ON_BOARDING_SHOWN] = shown
    }

    val getOnBoardingFragmentCounter: Flow<Int> = context.dataStore.data.map { preference ->
        preference[ON_BOARDING_FRAGMENT_COUNTER] ?: 0
    }

    suspend fun setOnBoardingFragmentCounter(counter: Int = 0) =
        context.dataStore.edit { preference ->
            preference[ON_BOARDING_FRAGMENT_COUNTER] = counter
        }
}
