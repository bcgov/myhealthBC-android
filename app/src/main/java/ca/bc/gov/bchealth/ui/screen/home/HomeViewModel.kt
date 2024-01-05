package ca.bc.gov.bchealth.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.home.HomeBannerItem
import ca.bc.gov.bchealth.ui.home.LaunchCheckStatus
import ca.bc.gov.repository.BannerRepository
import ca.bc.gov.repository.OnBoardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author pinakin.kansara
 * Created 2023-12-13 at 1:57 p.m.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val onBoardingRepository: OnBoardingRepository,
    private val bannerRepository: BannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private var isBiometricAuthenticationRequired: Boolean = true

    companion object {
        private const val TAG = "HomeViewModel"
    }

    fun launchCheck() = viewModelScope.launch {
        when {
            onBoardingRepository.isReOnBoardingRequired || onBoardingRepository.onBoardingRequired -> {
                _uiState.update { it.copy(launchCheckStatus = LaunchCheckStatus.REQUIRE_ON_BOARDING) }
            }

            isBiometricAuthenticationRequired -> {
                _uiState.update { it.copy(launchCheckStatus = LaunchCheckStatus.REQUIRE_BIOMETRIC_AUTHENTICATION) }
            }
        }
    }

    fun fetchBanner() = viewModelScope.launch {
        try {
            bannerRepository.getBanner()?.let { banner ->
                _uiState.update {
                    it.copy(
                        bannerItem = HomeBannerItem(
                            banner.title,
                            body = banner.body
                        )
                    )
                }
            }
        } catch (e: Exception) {
        }
    }

    fun resetUiState() {
        _uiState.update { it.copy(launchCheckStatus = null) }
        isBiometricAuthenticationRequired = false
    }
}

data class HomeUiState(
    val launchCheckStatus: LaunchCheckStatus? = null,
    val bannerItem: HomeBannerItem? = null,
)
