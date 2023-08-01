package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.quicklink.QuickLinkDto
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

    suspend fun updateManageableQuickLinks(quickLinks: List<QuickLinkDto>) {
        appFeatureDao.updateManageableQuickLinks(quickLinks.map { it.name })
    }

    suspend fun getQuickAccessFeatures(): List<AppFeatureDto> {
        return appFeatureDao.getQuickAccessFeatures().map {
            it.toDto()
        }
    }

    suspend fun getManageableAppFeatures() = appFeatureDao.getManageableAppFeatures()

    suspend fun getNonManageableAppFeatures() = appFeatureDao.getNonManageableAppFeatures()
}
