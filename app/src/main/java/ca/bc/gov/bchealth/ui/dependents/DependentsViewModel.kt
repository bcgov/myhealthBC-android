package ca.bc.gov.bchealth.ui.dependents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.repository.patient.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DependentsViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DependentsUiState())
    val uiState: StateFlow<DependentsUiState> = _uiState.asStateFlow()

    fun checkBcscAuthentication() = viewModelScope.launch {
        _uiState.update { DependentsUiState(onLoading = true) }
        try {
            patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
            _uiState.update { DependentsUiState(onLoading = false, isBcscAuthenticated = true) }
        } catch (e: Exception) {
            _uiState.update { DependentsUiState(onLoading = false, isBcscAuthenticated = false) }
        }
    }

    fun resetUiState() {
        _uiState.tryEmit(
            DependentsUiState(
                onLoading = false,
                isBcscAuthenticated = null
            )
        )
    }
}

data class DependentsUiState(
    val onLoading: Boolean = false,
    val isBcscAuthenticated: Boolean? = null,
)
