package ca.bc.gov.bchealth.ui.feeback

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.repository.FeedbackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val repository: FeedbackRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String) = viewModelScope.launch {
        try {
            repository.addFeedback(message)
        } catch (e: Exception) {
            handleBaseException(e)
        }
    }

    data class FeedbackUiState(
        val isLoading: Boolean = false,
        val requestSucceed: Boolean? = null,
    )
}
