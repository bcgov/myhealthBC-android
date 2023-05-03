package ca.bc.gov.bchealth.ui.feedback

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.repository.FeedbackRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val repository: FeedbackRepository,
    private val mobileConfigRepository: MobileConfigRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String) = viewModelScope.launch {
        try {
            _uiState.update { it.copy(isLoading = true) }
            mobileConfigRepository.refreshMobileConfiguration()
            repository.addFeedback(message)
            _uiState.update { it.copy(isLoading = false, requestSucceed = true) }
        } catch (e: Exception) {
            handleBaseException(e) { exception ->
                _uiState.update { it.copy(error = exception) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun resetUiState() {
        _uiState.update { FeedbackUiState() }
    }
}

data class FeedbackUiState(
    val isLoading: Boolean = false,
    val requestSucceed: Boolean? = null,
    val error: Exception? = null,
)
