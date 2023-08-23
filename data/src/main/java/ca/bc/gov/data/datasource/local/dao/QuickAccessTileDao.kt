package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import ca.bc.gov.data.datasource.local.entity.settings.QuickAccessTileEntity
import ca.bc.gov.data.datasource.local.entity.settings.QuickAccessTileShowAsQuickLinkEntity

@Dao
interface QuickAccessTileDao : BaseDao<QuickAccessTileEntity> {

    @Update(entity = QuickAccessTileEntity::class)
    suspend fun updateAll(tiles: List<QuickAccessTileShowAsQuickLinkEntity>): Int

    @Update(entity = QuickAccessTileEntity::class)
    suspend fun update(tile: QuickAccessTileShowAsQuickLinkEntity)

    @Query("UPDATE quick_access_tile SET show_as_quick_access  = :showAsQuickAccess")
    suspend fun update(showAsQuickAccess: Boolean)
}
