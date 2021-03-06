package ca.bc.gov.bchealth.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.repository.OnBoardingRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.bcsc.PostLoginCheck
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
    var isForceLogout: Boolean = false

    fun launchCheck() = viewModelScope.launch {
        if (bcscAuthRepo.checkSession()) {
            onBoardingRepository.onBCSCLoginRequiredPostBiometric = false
        }
        when {
            onBoardingRepository.onBoardingRequired -> {
                _uiState.update { state ->
                    state.copy(isLoading = false, isOnBoardingRequired = true)
                }
            }
            isAuthenticationRequired -> {
                _uiState.update { state -> state.copy(isAuthenticationRequired = true) }
            }
            onBoardingRepository.onBCSCLoginRequiredPostBiometric -> {
                _uiState.update { state -> state.copy(isBcscLoginRequiredPostBiometrics = true) }
            }
            bcscAuthRepo.getPostLoginCheck() == PostLoginCheck.IN_PROGRESS.name -> {
                _uiState.update { state -> state.copy(isForceLogout = true) }
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
        onBoardingRepository.onBCSCLoginRequiredPostBiometric = isRequired
        _uiState.update { state -> state.copy(isBcscLoginRequiredPostBiometrics = isRequired) }
    }

    fun onForceLogout(isRequired: Boolean) {
        isForceLogout = isRequired
        _uiState.update { state -> state.copy(isForceLogout = isRequired) }
    }

    fun getAuthenticatedPatientName() = viewModelScope.launch {
        try {
            val patient =
                patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
            val names = patient.fullName.split(" ")
            val firstName = if (names.isNotEmpty()) names.first() else ""
            _uiState.update {
                it.copy(patientFirstName = firstName)
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(patientFirstName = "")
            }
        }
    }

    suspend fun getHomeRecordsList(): MutableList<HomeRecordItem> {
        val isLoggedIn: Boolean = try {
            bcscAuthRepo.checkSession()
        } catch (e: Exception) {
            false
        }

        return mutableListOf(
            HomeRecordItem(
                R.drawable.ic_login_info,
                R.string.health_records,
                R.string.health_records_desc,
                0,
                if (isLoggedIn) R.string.view_records else R.string.get_started,
                HomeNavigationType.HEALTH_RECORD
            ),
            HomeRecordItem(
                R.drawable.ic_green_tick,
                R.string.health_passes,
                R.string.proof_of_vaccination_desc,
                R.drawable.ic_right_arrow,
                R.string.add_proofs,
                HomeNavigationType.VACCINE_PROOF
            ),
            HomeRecordItem(
                R.drawable.ic_resources,
                R.string.health_resources,
                R.string.resources_desc,
                R.drawable.ic_right_arrow,
                R.string.learn_more,
                HomeNavigationType.RESOURCES
            )
        )
    }

    fun executeOneTimeDataFetch() = bcscAuthRepo.executeOneTimeDatFetch()
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val isOnBoardingRequired: Boolean = false,
    val isAuthenticationRequired: Boolean = false,
    val isBcscLoginRequiredPostBiometrics: Boolean = false,
    val patientFirstName: String? = null,
    val isForceLogout: Boolean = false
)

data class HomeRecordItem(
    @DrawableRes val iconTitle: Int,
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val icon: Int,
    @StringRes val btnTitle: Int,
    val recordType: HomeNavigationType
)

enum class HomeNavigationType {
    HEALTH_RECORD,
    VACCINE_PROOF,
    RESOURCES
}
