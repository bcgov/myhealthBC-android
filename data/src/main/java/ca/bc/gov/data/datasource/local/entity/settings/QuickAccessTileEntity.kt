package ca.bc.gov.data.datasource.local.entity.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.QuickAccessLinkName

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
    ],
    indices = [Index(value = ["tile_name"], unique = true)]
)
data class QuickAccessTileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "feature_id")
    val featureId: Long,
    @ColumnInfo(name = "tile_name")
    val tileName: QuickAccessLinkName,
    @ColumnInfo(name = "tile_payload", defaultValue = "null")
    val tilePayload: String? = null,
    @ColumnInfo(name = "show_as_quick_access", defaultValue = "false")
    val showAsQuickAccess: Boolean = false
)
