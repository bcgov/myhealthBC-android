package ca.bc.gov.data.datasource.local.entity.notification

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "notification",
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "hdid")
    val hdid: String,

    @ColumnInfo("notificationId")
    val notificationId: String,

    @ColumnInfo("category")
    val category: String,

    @ColumnInfo("displayText")
    val displayText: String,

    @ColumnInfo("actionUrl")
    val actionUrl: String,

    @ColumnInfo("actionType")
    val actionType: String,

    @ColumnInfo("date")
    val date: String,
)
