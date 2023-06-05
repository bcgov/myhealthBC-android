package ca.bc.gov.data.datasource.local

import ca.bc.gov.data.datasource.local.dao.NotificationDao
import ca.bc.gov.data.datasource.local.entity.notification.NotificationEntity
import javax.inject.Inject

class NotificationLocalDataSource @Inject constructor(
    private val notificationDao: NotificationDao
) {

    suspend fun getNotifications(hdid: String) =
        notificationDao.getNotifications(hdid)

    suspend fun storeNotifications(notifications: List<NotificationEntity>) {
        notificationDao.insert(notifications)
    }

    suspend fun deleteNotifications(hdid: String): Int =
        notificationDao.deleteNotifications(hdid)
}
