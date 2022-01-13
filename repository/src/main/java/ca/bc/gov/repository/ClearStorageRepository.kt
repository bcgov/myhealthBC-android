package ca.bc.gov.repository

import ca.bc.gov.data.datasource.LocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class ClearStorageRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    suspend fun clearDataBase() = withContext(Dispatchers.IO){
        localDataSource.clearDataBase()
    }
}