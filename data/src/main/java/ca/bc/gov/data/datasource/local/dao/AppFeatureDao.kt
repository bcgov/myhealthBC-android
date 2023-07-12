package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.settings.AppFeatureEntity

@Dao
interface AppFeatureDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appFeatureEntity: AppFeatureEntity): Long

    @Query("DELETE FROM app_feature")
    suspend fun deleteAll()

    @Query("SELECT * FROM app_feature WHERE quick_access_enabled = 1 ")
    suspend fun getQuickAccessTiles(): List<AppFeatureEntity>

    @Query("SELECT * FROM app_feature WHERE is_management_enabled = 1 ")
    suspend fun getManageableTiles(): List<AppFeatureEntity>
}
