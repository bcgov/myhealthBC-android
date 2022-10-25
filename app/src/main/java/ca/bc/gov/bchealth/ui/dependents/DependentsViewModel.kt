package ca.bc.gov.bchealth.ui.dependents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.repository.DependentsRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.extensions.mapFlowContent
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
    private val bcscAuthRepo: BcscAuthRepo,
    dependentsRepository: DependentsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DependentsUiState())
    val uiState: StateFlow<DependentsUiState> = _uiState.asStateFlow()

    val dependentsList = dependentsRepository.getAllDependents().mapFlowContent { it.toUiModel() }

    fun loadAuthenticationState() = viewModelScope.launch {
        _uiState.update { DependentsUiState(onLoading = true) }

        try {
            checkAuthentication()
            val isSessionActive: Boolean = bcscAuthRepo.checkSession()
            _uiState.update {
                it.copy(onLoading = false, isSessionActive = isSessionActive)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it.copy(onLoading = false, isBcscAuthenticated = false) }
        }
    }

    private suspend fun checkAuthentication() {
        patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
        _uiState.update { it.copy(isBcscAuthenticated = true) }
    }

    fun resetUiState() {
        _uiState.tryEmit(
            DependentsUiState(
                onLoading = false,
                isBcscAuthenticated = null,
                isSessionActive = null
            )
        )
    }
}

data class DependentsUiState(
    val onLoading: Boolean = false,
    val isBcscAuthenticated: Boolean? = null,
    val isSessionActive: Boolean? = null,
)

data class DependentDetailItem(
    val id: String,
    val fullName: String,
)
