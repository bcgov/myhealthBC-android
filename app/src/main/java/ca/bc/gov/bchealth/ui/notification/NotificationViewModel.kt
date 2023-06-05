package ca.bc.gov.bchealth.ui.notification

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    val repository: NotificationRepository
) : BaseViewModel() {

    fun getNotifications() = viewModelScope.launch {
        try {
            val notifications = repository.loadNotifications()
        } catch (e: Exception) {
        }
    }
}
