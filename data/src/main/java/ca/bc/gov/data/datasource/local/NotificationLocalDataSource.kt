package ca.bc.gov.data.datasource.local

import ca.bc.gov.data.datasource.local.dao.NotificationDao
import ca.bc.gov.data.datasource.local.entity.notification.NotificationEntity
import java.time.Instant
import javax.inject.Inject

class NotificationLocalDataSource @Inject constructor(
    private val notificationDao: NotificationDao
) {

    suspend fun getNotifications(hdid: String, currentDate: Instant) =
        notificationDao.getNotifications(hdid, currentDate)

    suspend fun storeNotifications(notifications: List<NotificationEntity>) {
        notificationDao.insert(notifications)
    }

    suspend fun deleteNotifications(hdid: String): Int =
        notificationDao.deleteNotifications(hdid)
}
