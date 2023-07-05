package ca.bc.gov.data.datasource.remote

import ca.bc.gov.common.const.MESSAGE_INVALID_RESPONSE
import ca.bc.gov.common.const.SERVER_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.data.datasource.remote.api.HealthGatewayPrivateApi
import ca.bc.gov.data.utils.safeCall
import javax.inject.Inject

class NotificationRemoteDataSource @Inject constructor(
    private val healthGatewayPrivateApi: HealthGatewayPrivateApi
) {

    suspend fun fetchNotifications(hdid: String) =
        safeCall {
            healthGatewayPrivateApi.fetchNotifications(hdid)
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

    suspend fun deleteNotifications(hdid: String) =
        safeCall {
            healthGatewayPrivateApi.deleteNotifications(hdid)
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)

    suspend fun deleteNotification(hdid: String, notificationId: String) =
        safeCall {
            healthGatewayPrivateApi.deleteNotification(hdid, notificationId)
        } ?: throw MyHealthException(SERVER_ERROR, MESSAGE_INVALID_RESPONSE)
}
