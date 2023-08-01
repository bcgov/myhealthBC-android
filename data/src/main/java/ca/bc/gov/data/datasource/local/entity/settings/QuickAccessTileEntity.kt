package ca.bc.gov.data.datasource.local.entity.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "quick_access_tile",
)
data class QuickAccessTileEntity(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "modules")
    val modules: List<String>,
)
