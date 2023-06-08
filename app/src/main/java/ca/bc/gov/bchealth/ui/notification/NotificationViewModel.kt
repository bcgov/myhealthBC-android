package ca.bc.gov.bchealth.ui.notification

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.common.model.notification.NotificationActionTypeDto
import ca.bc.gov.common.utils.toDateTimeString
import ca.bc.gov.common.utils.toLocalDateTimeInstant
import ca.bc.gov.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    val repository: NotificationRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUIState())
    val uiState: StateFlow<NotificationsUIState> = _uiState.asStateFlow()

    fun getNotifications() = viewModelScope.launch {
        try {
            _uiState.update { it.copy(loading = true) }

            val currentDate = Instant.now().toLocalDateTimeInstant() ?: Instant.now()
            val notifications = repository.loadNotifications(currentDate)

            _uiState.update {
                it.copy(
                    loading = false,
                    list = notifications.map { dto ->
                        NotificationItem(
                            notificationId = dto.notificationId,
                            content = dto.displayText,
                            date = dto.date.toDateTimeString(),
                            actionType = dto.actionType,
                            actionUrl = dto.actionUrl
                        )
                    }
                )
            }
        } catch (e: Exception) {
            _uiState.update { NotificationsUIState(loading = false) }
            e.printStackTrace()
        }
    }

    fun deleteNotifications() = viewModelScope.launch {
        try {
            _uiState.update { it.copy(loading = true) }

            repository.deleteNotifications()

            _uiState.update { it.copy(loading = false) }
        } catch (e: Exception) {
            _uiState.update { NotificationsUIState(loading = false) }
            e.printStackTrace()
        }
    }

    fun deleteNotification(notificationId: String) = viewModelScope.launch {
        try {
            _uiState.update { it.copy(loading = true) }

            repository.deleteNotification(notificationId = notificationId)

            _uiState.update { it.copy(loading = false) }
        } catch (e: Exception) {
            _uiState.update { NotificationsUIState(loading = false) }
            e.printStackTrace()
        }
    }

    data class NotificationsUIState(
        val loading: Boolean = false,
        val list: List<NotificationItem> = listOf(),
    )

    data class NotificationItem(
        val notificationId: String,
        val content: String,
        val actionUrl: String,
        val actionType: NotificationActionTypeDto,
        val date: String,
    )
}
