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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DependentsViewModel @Inject constructor(
    private val dependentsRepository: DependentsRepository,
    private val patientRepository: PatientRepository,
    private val bcscAuthRepo: BcscAuthRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(DependentsUiState())
    val uiState: StateFlow<DependentsUiState> = _uiState.asStateFlow()

    val dependentsList = dependentsRepository.getAllDependents().mapFlowContent {
        it.toUiModel(LocalDate.now())
    }

    fun removeDependent(patientId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(onLoading = true) }
            try {
                dependentsRepository.deleteDependent(patientId)
                _uiState.update { it.copy(onLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e, onLoading = false) }
            }
        }
    }

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

    fun hideLoadingState() {
        _uiState.update { it.copy(onLoading = false) }
    }

    fun displayLoadingState() {
        _uiState.update { it.copy(onLoading = true) }
    }

    fun resetUiState() {
        _uiState.tryEmit(
            DependentsUiState(
                onLoading = false,
                isBcscAuthenticated = null,
                isSessionActive = null,
                error = null,
            )
        )
    }

    fun resetErrorState() {
        _uiState.update { it.copy(error = null) }
    }
}

data class DependentsUiState(
    val onLoading: Boolean = false,
    val isBcscAuthenticated: Boolean? = null,
    val isSessionActive: Boolean? = null,
    val error: Exception? = null
)

data class DependentDetailItem(
    val patientId: Long,
    val hdid: String,
    val firstName: String,
    val fullName: String,
    val agedOut: Boolean
)
