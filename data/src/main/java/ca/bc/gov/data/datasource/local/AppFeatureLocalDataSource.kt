package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.data.datasource.local.dao.AppFeatureDao
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

class AppFeatureLocalDataSource @Inject constructor(
    private val appFeatureDao: AppFeatureDao
) {

    suspend fun insertIfEmpty(appFeatures: List<AppFeatureDto>) {
        val count = appFeatureDao.countRegisters()
        if (count == 0) {
            appFeatures.forEach { dto ->
                appFeatureDao.insert(dto.toEntity())
            }
        }
    }

    suspend fun updateQuickAccessFlag(id: Long, enabled: Boolean) {
        appFeatureDao.updateQuickAccessFlag(id, enabled)
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

    suspend fun deleteAll() {
        appFeatureDao.deleteAll()
    }
}
