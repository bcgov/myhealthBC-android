package ca.bc.gov.data.datasource.local.entity.dependent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dependent_list_order")
data class DependentListOrder(
    @PrimaryKey
    @ColumnInfo(name = "hdid")
    val hdid: String,

    @ColumnInfo(name = "list_order")
    val order: Int,
)
