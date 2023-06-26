package ca.bc.gov.data.datasource.local

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
}
