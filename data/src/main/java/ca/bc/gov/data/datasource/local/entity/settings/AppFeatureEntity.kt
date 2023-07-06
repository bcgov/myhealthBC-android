package ca.bc.gov.data.datasource.local.entity.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_feature",
    indices = [Index(value = ["feature_name_id", "feature_icon_id"], unique = true)]
)
data class AppFeatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "feature_name_id")
    val featureNameId: Int,
    @ColumnInfo(name = "feature_icon_id")
    val featureIconId: Int,
    @ColumnInfo(name = "destination_id")
    val destinationId: Int,
    @ColumnInfo(name = "enabled", defaultValue = "false")
    val isEnabled: Boolean = false,
    @ColumnInfo(name = "quick_access_enabled", defaultValue = "false")
    val isQuickAccessEnabled: Boolean = false
)