package ca.bc.gov.data.datasource.local

import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class LocalDataSource @Inject constructor(
    private val db: MyHealthDataBase
) {

    suspend fun clearDataBase() = db.clearAllTables()
}
