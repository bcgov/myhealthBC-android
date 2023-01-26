package ca.bc.gov.bchealth.ui

import androidx.lifecycle.ViewModel
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.exceptions.ServiceDownException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel : ViewModel() {
    private val _baseUiState = MutableStateFlow(BaseUiState())
    val baseUiState: StateFlow<BaseUiState> = _baseUiState.asStateFlow()

    fun handleBaseException(e: Exception) {
        when (e) {
            is NetworkConnectionException -> _baseUiState.update {
                it.copy(connected = false)
            }

            is ServiceDownException -> _baseUiState.update {
                it.copy(serviceUp = false)
            }
        }
    }

    fun resetBaseUiState() {
        _baseUiState.update { BaseUiState() }
    }
}

data class BaseUiState(
    val connected: Boolean = true,
    val serviceUp: Boolean = true,
)
