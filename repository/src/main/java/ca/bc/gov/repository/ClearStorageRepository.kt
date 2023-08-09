package ca.bc.gov.repository

import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.data.datasource.local.LocalDataSource
import ca.bc.gov.preference.EncryptedPreferenceStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class ClearStorageRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val preferenceStorage: EncryptedPreferenceStorage,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun clearDataBase() = withContext(ioDispatcher) {
        localDataSource.clearDataBase()
    }

    suspend fun clearPreferences() = withContext(ioDispatcher) {
        preferenceStorage.clear()
    }

    suspend fun clearMedicationPreferences() = withContext(ioDispatcher) {
        preferenceStorage.protectiveWord = null
        preferenceStorage.protectiveWordState =
            ProtectiveWordState.PROTECTIVE_WORD_NOT_REQUIRED.value
    }
}
