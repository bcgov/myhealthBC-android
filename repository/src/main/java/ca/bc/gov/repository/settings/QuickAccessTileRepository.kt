package ca.bc.gov.repository.settings

import ca.bc.gov.common.model.QuickAccessTileShowAsQuickLinkDto
import ca.bc.gov.common.model.settings.QuickAccessTileDto
import ca.bc.gov.data.datasource.local.QuickActionTileLocalDataSource
import javax.inject.Inject

class QuickAccessTileRepository @Inject constructor(
    private val quickActionTileLocalDataSource: QuickActionTileLocalDataSource
) {

    suspend fun insert(quickAccessTileDto: QuickAccessTileDto) =
        quickActionTileLocalDataSource.insert(quickAccessTileDto)

    suspend fun insertAll(tiles: List<QuickAccessTileDto>) =
        quickActionTileLocalDataSource.insertAll(tiles)

    suspend fun updateAll(tiles: List<QuickAccessTileShowAsQuickLinkDto>) =
        quickActionTileLocalDataSource.updateAll(tiles)

    suspend fun update(tile: QuickAccessTileShowAsQuickLinkDto) =
        quickActionTileLocalDataSource.update(tile)

    suspend fun update(showAsQuickAccess: Boolean) =
        quickActionTileLocalDataSource.update(showAsQuickAccess)
}
