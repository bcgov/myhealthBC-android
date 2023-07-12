package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.settings.AppFeatureDto
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

    suspend fun getQuickAccessTiles(): List<AppFeatureDto> {
        return appFeatureDao.getQuickAccessTiles().map {
            it.toDto()
        }
    }

    suspend fun getManageableTiles(): List<AppFeatureDto> {
        return appFeatureDao.getManageableTiles().map {
            it.toDto()
        }
    }
}
