package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.notification.NotificationEntity
import java.time.Instant

@Dao
interface NotificationDao : BaseDao<NotificationEntity> {

    @Query("SELECT * FROM notification WHERE hdid = :hdid AND date < :currentDate ORDER BY date DESC")
    suspend fun getNotifications(
        hdid: String,
        currentDate: Instant
    ): List<NotificationEntity>

    @Query("DELETE FROM notification WHERE hdid = :hdid")
    suspend fun deleteNotifications(hdid: String): Int
}
