package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.QuickAccessTileShowAsQuickLinkDto
import ca.bc.gov.common.model.settings.QuickAccessTileDto
import ca.bc.gov.data.datasource.local.dao.QuickAccessTileDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

class QuickActionTileLocalDataSource @Inject constructor(
    private val quickAccessTileDao: QuickAccessTileDao
) {

    suspend fun insert(quickAccessTileDto: QuickAccessTileDto): Long {
        return quickAccessTileDao.insert(quickAccessTileDto.toEntity())
    }

    suspend fun insertAll(tiles: List<QuickAccessTileDto>): List<Long> {
        return quickAccessTileDao.insert(tiles.map { it.toEntity() })
    }

    suspend fun updateAll(tiles: List<QuickAccessTileShowAsQuickLinkDto>): Int {
        return quickAccessTileDao.updateAll(tiles.map { it.toEntity() })
    }
    suspend fun update(tile: QuickAccessTileShowAsQuickLinkDto) {
        quickAccessTileDao.update(tile.toEntity())
    }

    suspend fun update(showAsQuickAction: Boolean) {
        quickAccessTileDao.update(showAsQuickAction)
    }
}
