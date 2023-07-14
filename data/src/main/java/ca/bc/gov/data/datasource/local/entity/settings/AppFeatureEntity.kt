package ca.bc.gov.data.datasource.local.entity.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_feature",
)
data class AppFeatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "feature_name")
    val featureName: String?,
    @ColumnInfo(name = "feature_name_id")
    val featureNameId: Int?,
    @ColumnInfo(name = "category_name_id")
    val categoryNameId: Int,
    @ColumnInfo(name = "feature_icon_id")
    val featureIconId: Int,
    @ColumnInfo(name = "destination_id")
    val destinationId: Int,
    @ColumnInfo(name = "destination_param")
    val destinationParam: String?,
    @ColumnInfo(name = "is_management_enabled", defaultValue = "false")
    val isManagementEnabled: Boolean = false,
    @ColumnInfo(name = "quick_access_enabled", defaultValue = "false")
    val isQuickAccessEnabled: Boolean = false
)
