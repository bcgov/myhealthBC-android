package ca.bc.gov.repository

import ca.bc.gov.data.datasource.LocalDataSource
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class ClearStorageRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    suspend fun clearDataBase() = localDataSource.clearDataBase()
}