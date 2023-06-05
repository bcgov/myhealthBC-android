package ca.bc.gov.repository

import ca.bc.gov.data.datasource.remote.NotificationRemoteDataSource
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
    private val bcscAuthRepo: BcscAuthRepo,
) {
    suspend fun getNotifications() {
        val authParameters = bcscAuthRepo.getAuthParametersDto()
        notificationRemoteDataSource.fetchNotifications(
            hdid = authParameters.hdid,
        )
    }
}
