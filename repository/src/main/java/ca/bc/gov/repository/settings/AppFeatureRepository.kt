package ca.bc.gov.repository.settings

import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.data.datasource.local.AppFeatureLocalDataSource
import javax.inject.Inject

class AppFeatureRepository @Inject constructor(
    private val appFeatureLocalDataSource: AppFeatureLocalDataSource
) {
    suspend fun insert(appFeatures: List<AppFeatureDto>) {
        appFeatureLocalDataSource.insertIfEmpty(appFeatures)
    }

    suspend fun updateQuickAccessFlag(id: Long, enabled: Boolean) {
        appFeatureLocalDataSource.updateQuickAccessFlag(id, enabled)
    }

    suspend fun getQuickAccessTiles() = appFeatureLocalDataSource.getQuickAccessTiles()

    suspend fun getManageableTiles() = appFeatureLocalDataSource.getManageableTiles()
}
