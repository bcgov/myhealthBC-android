package ca.bc.gov.repository.settings

import ca.bc.gov.data.datasource.local.QuickActionTileLocalDataSource
import javax.inject.Inject

class QuickAccessTileRepository @Inject constructor(
    private val quickActionTileLocalDataSource: QuickActionTileLocalDataSource
)
