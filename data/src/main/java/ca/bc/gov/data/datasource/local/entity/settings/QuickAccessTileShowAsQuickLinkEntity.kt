package ca.bc.gov.data.datasource.local.entity.settings

import androidx.room.ColumnInfo

data class QuickAccessTileShowAsQuickLinkEntity(
    val id: Long = 0,
    @ColumnInfo(name = "show_as_quick_access", defaultValue = "false")
    val showAsQuickAccess: Boolean = false
)
