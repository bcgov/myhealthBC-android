package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.quicklink.QuickLinkDto
import ca.bc.gov.data.datasource.local.dao.QuickAccessTileDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

class QuickActionTileLocalDataSource @Inject constructor(
    private val quickAccessTileDao: QuickAccessTileDao
) {
    suspend fun update(quickAccessTiles: List<QuickLinkDto>) {
        quickAccessTileDao.deleteAll()
        quickAccessTiles.forEach {
            quickAccessTileDao.insert(it.toEntity())
        }
    }
}
