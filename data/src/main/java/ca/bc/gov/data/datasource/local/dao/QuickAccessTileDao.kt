package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.settings.QuickAccessTileEntity

@Dao
interface QuickAccessTileDao : BaseDao<QuickAccessTileEntity> {

    @Query("DELETE FROM quick_access_tile")
    suspend fun deleteAll()
}
