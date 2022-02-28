package ca.bc.gov.repository

import ca.bc.gov.data.datasource.local.LocalDataSource
import ca.bc.gov.data.local.preference.EncryptedPreferenceStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class ClearStorageRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val preferenceStorage: EncryptedPreferenceStorage
) {

    suspend fun clearDataBase() = withContext(Dispatchers.IO) {
        localDataSource.clearDataBase()
    }

    suspend fun clearPreferences() = withContext(Dispatchers.IO) {
        preferenceStorage.clear()
    }
}
