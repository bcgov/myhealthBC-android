package ca.bc.gov.bchealth.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.OnBoardingRepository
import ca.bc.gov.repository.patient.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val onBoardingRepository: OnBoardingRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    var isAuthenticationRequired: Boolean = true
    var isBcscLoginRequiredPostBiometrics: Boolean = false

    fun launchCheck() = viewModelScope.launch {
        when {
            onBoardingRepository.onBoardingRequired -> {
                isBcscLoginRequiredPostBiometrics = true
                _uiState.update { state ->
                    state.copy(isLoading = false, isOnBoardingRequired = true)
                }
            }
            isAuthenticationRequired -> {
                _uiState.update { state -> state.copy(isAuthenticationRequired = true) }
            }
            isBcscLoginRequiredPostBiometrics -> {
                _uiState.update { state -> state.copy(isBcscLoginRequiredPostBiometrics = true) }
            }
        }
    }

    fun onBoardingShown() {
        _uiState.update {
            it.copy(isOnBoardingRequired = false)
        }
    }

    fun onAuthenticationRequired(isRequired: Boolean) {
        isAuthenticationRequired = isRequired
        _uiState.update { state -> state.copy(isAuthenticationRequired = isRequired) }
    }

    fun onBcscLoginRequired(isRequired: Boolean) {
        isBcscLoginRequiredPostBiometrics = isRequired
        _uiState.update { state -> state.copy(isBcscLoginRequiredPostBiometrics = isRequired) }
    }

    fun getPatientFirstName() = viewModelScope.launch {
        val fullName = patientRepository.getAuthenticatedPatient().fullName
        val nameList = fullName.split(" ")
        var patientFirstName = ""
        if (nameList.isNotEmpty()) {
            patientFirstName = nameList.first()
        }
        _uiState.update { state ->
            state.copy(
                patientFirstName = patientFirstName
            )
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val isOnBoardingRequired: Boolean = false,
    val isAuthenticationRequired: Boolean = false,
    val isBcscLoginRequiredPostBiometrics: Boolean = false,
    val patientFirstName: String? = null
)

data class HomeRecordItem(
    val iconTitle: Int,
    val title: String,
    val description: String,
    val icon: Int,
    val btnTitle: String,
    val recordType: HomeNavigationType
)

enum class HomeNavigationType {
    HEALTH_RECORD,
    VACCINE_PROOF,
    RESOURCES
}
