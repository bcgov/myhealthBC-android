package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.common.model.settings.AppFeatureWithQuickAccessTilesDto
import ca.bc.gov.data.datasource.local.dao.AppFeatureDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

class AppFeatureLocalDataSource @Inject constructor(
    private val appFeatureDao: AppFeatureDao
) {

    suspend fun insert(appFeatureDto: AppFeatureDto): Long {
        return appFeatureDao.insert(appFeatureDto.toEntity())
    }

    suspend fun getAppFeaturesWithQuickAccessTiles(): List<AppFeatureWithQuickAccessTilesDto> {
        return appFeatureDao.getAllFeatureWithQuickAccessTiles().map { it.toDto() }
    }
}
