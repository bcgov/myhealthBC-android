package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import ca.bc.gov.data.datasource.local.entity.settings.QuickAccessTileEntity

@Dao
interface QuickAccessTileDao : BaseDao<QuickAccessTileEntity>
