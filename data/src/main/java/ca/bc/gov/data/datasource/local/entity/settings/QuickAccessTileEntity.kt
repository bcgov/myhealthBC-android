package ca.bc.gov.data.datasource.local.entity.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "quick_access_tile",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = AppFeatureEntity::class,
            parentColumns = ["id"],
            childColumns = ["feature_id"],
            onDelete = androidx.room.ForeignKey.CASCADE,
            onUpdate = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class QuickAccessTileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "feature_id")
    val featureId: Long,
    @ColumnInfo(name = "tile_name_id")
    val tileNameId: Int,
    @ColumnInfo(name = "tile_icon_id")
    val tileIconId: Int,
    @ColumnInfo(name = "enabled", defaultValue = "false")
    val isEnabled: Boolean = false
)
