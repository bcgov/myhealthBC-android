package ca.bc.gov.repository.settings

import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.data.datasource.local.AppFeatureLocalDataSource
import javax.inject.Inject

class AppFeatureRepository @Inject constructor(
    private val appFeatureLocalDataSource: AppFeatureLocalDataSource
) {
    suspend fun insert(appFeatureDto: AppFeatureDto) {
        appFeatureLocalDataSource.insert(appFeatureDto)
    }

    suspend fun getQuickAccessTiles() = appFeatureLocalDataSource.getQuickAccessTiles()

    suspend fun getManageableTiles() = appFeatureLocalDataSource.getManageableTiles()
}
