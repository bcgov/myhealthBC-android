package ca.bc.gov.bchealth.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.repository.OnBoardingRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
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
    private val patientRepository: PatientRepository,
    private val bcscAuthRepo: BcscAuthRepo
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
        val isLoggedIn: Boolean = try {
            bcscAuthRepo.checkLogin()
        } catch (e: Exception) {
            false
        }
        var patientFirstName = ""
        if (isLoggedIn) {
            val fullName = patientRepository.getAuthenticatedPatient().fullName
            val nameList = fullName.split(" ")
            if (nameList.isNotEmpty()) {
                patientFirstName = nameList.first()
            }
        } else {
            patientFirstName = ""
        }
        _uiState.update { state ->
            state.copy(
                patientFirstName = patientFirstName
            )
        }
    }

    fun getHomeRecordsList() = mutableListOf(
        HomeRecordItem(
            R.drawable.ic_login_info,
            "Health Records",
            "View and manage all your available health records, including dispensed medications, health visits, COVID-19 test results, immunizations and more.",
            R.drawable.ic_bcsc,
            "Get started",
            HomeNavigationType.HEALTH_RECORD
        ),
        HomeRecordItem(
            R.drawable.ic_green_tick,
            "Proof of vaccination",
            "View, download and print your BC Vaccine Card and federal proof of vaccination, to access events, businesses, services and to travel.",
            R.drawable.ic_right_arrow,
            "Add proofs",
            HomeNavigationType.VACCINE_PROOF
        ),
        HomeRecordItem(
            R.drawable.ic_resources,
            "Resources",
            "Find useful information and learn how to get vaccinated or tested for COVID-19.",
            R.drawable.ic_right_arrow,
            "Learn more",
            HomeNavigationType.RESOURCES
        )
    )
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
