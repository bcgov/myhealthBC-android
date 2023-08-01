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

    @Query(
        "UPDATE app_feature " +
            "SET show_as_quick_access = " +
            "CASE WHEN feature_name IN (:quickLinks) THEN 1 " +
            "ELSE 0 OR NOT has_manageable_quick_access_links END"
    )
    suspend fun updateManageableQuickLinks(quickLinks: List<String>)

    @Query("DELETE FROM app_feature")
    suspend fun deleteAll()

    @Query("SELECT * FROM app_feature where has_manageable_quick_access_links = 1")
    suspend fun getManageableAppFeatures(): List<AppFeatureEntity>

    @Query("SELECT * FROM app_feature where has_manageable_quick_access_links = 0")
    suspend fun getNonManageableAppFeatures(): List<AppFeatureEntity>

    @Query("SELECT * FROM app_feature where show_as_quick_access = 1")
    suspend fun getQuickAccessFeatures(): List<AppFeatureEntity>
}
