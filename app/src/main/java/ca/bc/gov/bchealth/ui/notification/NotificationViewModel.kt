package ca.bc.gov.bchealth.ui.notification

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.common.model.notification.NotificationDto
import ca.bc.gov.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
            val notifications = repository.loadNotifications()
            _uiState.update { it.copy(loading = false, list = notifications) }
        } catch (e: Exception) {
            _uiState.update { NotificationsUIState(loading = false) }
            e.printStackTrace()
        }
    }

    data class NotificationsUIState(
        val loading: Boolean = false,
        val list: List<NotificationDto> = listOf(),
    )
}
