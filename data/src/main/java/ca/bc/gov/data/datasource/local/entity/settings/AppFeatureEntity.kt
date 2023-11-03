package ca.bc.gov.data.datasource.local.entity.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.AppFeatureName

@Entity(
    tableName = "app_feature",
    indices = [Index(value = ["feature_name"], unique = true)]
)
data class AppFeatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "feature_name")
    val name: AppFeatureName,
    @ColumnInfo(name = "has_manageable_quick_access_links", defaultValue = "false")
    val hasManageableQuickAccessLinks: Boolean = false,
    @ColumnInfo(name = "show_as_quick_access", defaultValue = "false")
    val showAsQuickAccess: Boolean = false
)
