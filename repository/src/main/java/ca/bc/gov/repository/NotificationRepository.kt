package ca.bc.gov.repository

import ca.bc.gov.common.model.notification.NotificationDto
import ca.bc.gov.data.datasource.local.NotificationLocalDataSource
import ca.bc.gov.data.datasource.remote.NotificationRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.data.model.mapper.toEntity
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import java.time.Instant
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationLocalDataSource: NotificationLocalDataSource,
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
    private val bcscAuthRepo: BcscAuthRepo,
) {

    suspend fun refreshNotifications(hdid: String) {
        val notificationsDto = notificationRemoteDataSource.fetchNotifications(
            hdid = hdid,
        ).map { it.toDto(hdid) }

        notificationLocalDataSource.deleteNotifications(hdid)
        notificationLocalDataSource.storeNotifications(notificationsDto.map { it.toEntity() })
    }

    suspend fun loadNotifications(currentDate: Instant): List<NotificationDto> {
        val hdid = bcscAuthRepo.getAuthParametersDto().hdid
        return notificationLocalDataSource.getNotifications(hdid = hdid, currentDate = currentDate)
            .map { it.toDto() }
    }
}
