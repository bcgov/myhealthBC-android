package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.notification.NotificationEntity

@Dao
interface NotificationDao : BaseDao<NotificationEntity> {

    @Query("SELECT * FROM notification WHERE hdid = :hdid")
    suspend fun getNotifications(hdid: String): List<NotificationEntity>

    @Query("DELETE FROM notification WHERE hdid = :hdid")
    suspend fun deleteNotifications(hdid: String): Int
}
