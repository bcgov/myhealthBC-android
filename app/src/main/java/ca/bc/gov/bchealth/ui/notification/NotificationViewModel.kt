package ca.bc.gov.bchealth.ui.notification

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.common.exceptions.MyHealthAuthException
import ca.bc.gov.common.model.notification.NotificationActionTypeDto
import ca.bc.gov.common.utils.toDateTimeString
import ca.bc.gov.common.utils.toLocalDateTimeInstant
import ca.bc.gov.repository.NotificationRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.worker.MobileConfigRepository
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
    val repository: NotificationRepository,
    val mobileConfigRepository: MobileConfigRepository,
    val bcscAuthRepo: BcscAuthRepo
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUIState())
    val uiState: StateFlow<NotificationsUIState> = _uiState.asStateFlow()

    fun getNotifications() = viewModelScope.launch {
        resetErrorState()
        loadNotification()
    }

    private suspend fun loadNotification() {
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
            _uiState.update { it.copy(loading = false) }
            handleBaseException(e) {
                if (e is MyHealthAuthException) {
                    _uiState.update { it.copy(sessionExpired = true) }
                } else {
                    _uiState.update { it.copy(listError = false) }
                }
            }
            e.printStackTrace()
        }
    }

    fun deleteNotifications() = viewModelScope.launch {
        try {
            _uiState.update { it.copy(loading = true) }

            mobileConfigRepository.refreshMobileConfiguration()
            repository.deleteNotifications()

            loadNotification()
        } catch (e: Exception) {
            _uiState.update { it.copy(loading = false) }
            handleBaseException(e) {
                if (e is MyHealthAuthException) {
                    _uiState.update { it.copy(sessionExpired = true) }
                } else {
                    _uiState.update { it.copy(dismissError = false) }
                }
            }
            e.printStackTrace()
        }
    }

    fun deleteNotification(notificationId: String) = viewModelScope.launch {
        try {
            _uiState.update { it.copy(loading = true) }

            mobileConfigRepository.refreshMobileConfiguration()
            repository.deleteNotification(notificationId = notificationId)

            loadNotification()
        } catch (e: Exception) {
            _uiState.update { it.copy(loading = false) }
            handleBaseException(e) {
                if (e is MyHealthAuthException) {
                    _uiState.update { it.copy(sessionExpired = true) }
                } else {
                    _uiState.update { it.copy(dismissError = false) }
                }
            }
            e.printStackTrace()
        }
    }

    fun resetErrorState() {
        _uiState.update { it.copy(dismissError = false, listError = false, sessionExpired = false) }
    }

    data class NotificationsUIState(
        val loading: Boolean = false,
        val list: List<NotificationItem> = listOf(),
        val listError: Boolean = false,
        val dismissError: Boolean = false,
        val sessionExpired: Boolean = false,
    )

    data class NotificationItem(
        val notificationId: String,
        val content: String,
        val actionUrl: String,
        val actionType: NotificationActionTypeDto,
        val date: String,
    )
}
