package ca.bc.gov.bchealth.ui.feeback

import ca.bc.gov.bchealth.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedbackViewModel : BaseViewModel() {
    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String) {
        try {
            // todo: next ticket
        } catch (e: Exception) {
            handleBaseException(e)
        }
    }

    data class FeedbackUiState(
        val isLoading: Boolean = false,
        val requestSucceed: Boolean? = null,
    )
}
