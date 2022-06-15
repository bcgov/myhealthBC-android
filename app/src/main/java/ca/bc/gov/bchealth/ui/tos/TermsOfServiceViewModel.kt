package ca.bc.gov.bchealth.ui.tos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.repository.TermsOfServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class TermsOfServiceViewModel @Inject constructor(
    private val termsOfServiceRepository: TermsOfServiceRepository
) : ViewModel() {
    private val _tosUiState = MutableStateFlow(TermsOfServiceUiModel())
    val tosUiState: StateFlow<TermsOfServiceUiModel> = _tosUiState.asStateFlow()

    fun getTermsOfServices() = viewModelScope.launch {
        _tosUiState.update { it.copy(showLoading = true) }
        try {
            val tos = termsOfServiceRepository.getTermsOfService()
            _tosUiState.update {
                it.copy(
                    showLoading = false,
                    tos = tos.content,
                    termsOfServiceId = tos.id
                )
            }
        } catch (e: Exception) {
            when (e) {
                is NetworkConnectionException -> {
                    _tosUiState.update {
                        it.copy(
                            showLoading = false,
                            isConnected = false
                        )
                    }
                }
                is MustBeQueuedException -> {
                    _tosUiState.update {
                        it.copy(
                            showLoading = true,
                            onMustBeQueued = true,
                            queItUrl = e.message,
                        )
                    }
                }
                else -> {
                    _tosUiState.update {
                        it.copy(
                            showLoading = false,
                            isError = true
                        )
                    }
                }
            }
        }
    }
}

data class TermsOfServiceUiModel(
    val showLoading: Boolean = false,
    val isError: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val tos: String? = null,
    val termsOfServiceId: String? = null,
    val isConnected: Boolean = true
)
