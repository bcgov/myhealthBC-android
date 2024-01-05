package ca.bc.gov.bchealth.ui.screen.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.home.HomeBannerItem
import ca.bc.gov.bchealth.ui.home.LaunchCheckStatus
import ca.bc.gov.bchealth.ui.home.QuickAccessTileItem
import ca.bc.gov.common.model.AppFeatureName
import ca.bc.gov.common.model.UserAuthenticationStatus
import ca.bc.gov.repository.BannerRepository
import ca.bc.gov.repository.OnBoardingRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.settings.AppFeatureWithQuickAccessTilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author pinakin.kansara
 * Created 2023-12-13 at 1:57 p.m.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appFeatureWithQuickAccessTilesRepository: AppFeatureWithQuickAccessTilesRepository,
    private val onBoardingRepository: OnBoardingRepository,
    private val bannerRepository: BannerRepository,
    private val bcscAuthRepo: BcscAuthRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isQuickAccessTileTutorialRequired = appFeatureWithQuickAccessTilesRepository.isQuickAccessTileTutorialRequired))
    val uiState: StateFlow<HomeUiState> = _uiState

    private var isBiometricAuthenticationRequired: Boolean = true

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        viewModelScope.launch {
            bcscAuthRepo.userAuthenticationStatus.distinctUntilChanged()
                .collect { userAuthenticationStatus ->
                    _uiState.update {
                        it.copy(
                            loginInfoCardData = getLoginInfoCardData(
                                userAuthenticationStatus
                            ),
                            userAuthenticationStatus = userAuthenticationStatus,
                            quickAccessTileItems = loadQuickAccessTiles(userAuthenticationStatus)
                        )
                    }
                }
        }
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

    private fun getLoginInfoCardData(loginStatus: UserAuthenticationStatus): LoginInfoCardData? {

        return when (loginStatus) {

            UserAuthenticationStatus.AUTHENTICATED -> {
                null
            }

            UserAuthenticationStatus.SESSION_TIME_OUT -> {
                LoginInfoCardData(
                    title = R.string.session_time_out,
                    description = R.string.login_to_view_hidden_records_msg,
                    buttonText = R.string.log_in
                )
            }

            UserAuthenticationStatus.UN_AUTHENTICATED -> {
                LoginInfoCardData(
                    title = R.string.log_in_with_bc_services_card,
                    description = R.string.log_in_description,
                    buttonText = R.string.get_started,
                    image = R.drawable.img_un_authenticated_home_screen
                )
            }
        }
    }

    suspend fun loadQuickAccessTiles(loginStatus: UserAuthenticationStatus): List<QuickAccessTileItem> {
        val quickAccessTileItems = mutableListOf<QuickAccessTileItem>()
        val data = appFeatureWithQuickAccessTilesRepository.getAppFeaturesWithQuickAccessTiles()

        val appFeatures =
            appFeatureWithQuickAccessTilesRepository.getAppFeaturesWithQuickAccessTiles()
                .filter { it.appFeatureDto.showAsQuickAccess }
                .map {
                    QuickAccessTileItem.FeatureTileItem.from(it.appFeatureDto)
                }

        quickAccessTileItems.addAll(appFeatures)

        if (loginStatus == UserAuthenticationStatus.AUTHENTICATED) {
            data.filter { it.appFeatureDto.hasManageableQuickAccessLinks }.forEach {
                val quickLink = it.quickAccessTiles.filter { tile -> tile.showAsQuickAccess }
                    .map { tile -> QuickAccessTileItem.QuickLinkTileItem.from(tile) }
                quickAccessTileItems.addAll(quickLink)
            }

            quickAccessTileItems.removeIf { tile ->
                (tile.name == AppFeatureName.IMMUNIZATION_SCHEDULES.value)
            }
            quickAccessTileItems.find { it.name == AppFeatureName.RECOMMENDED_IMMUNIZATIONS.value }
                ?.let {
                    val index = quickAccessTileItems.indexOf(it)
                    if (index != 1) {
                        quickAccessTileItems.removeAt(index)
                        quickAccessTileItems.add(1, it)
                    }
                }
        }

        quickAccessTileItems.removeIf {
            (it.name == AppFeatureName.RECOMMENDED_IMMUNIZATIONS.value && (loginStatus != UserAuthenticationStatus.AUTHENTICATED))
        }

        return quickAccessTileItems
    }

    fun dismissBanner() {
        _uiState.update {
            it.copy(bannerItem = it.bannerItem?.copy(isDismissed = true))
        }
    }

    fun tutorialDismissed() {
        appFeatureWithQuickAccessTilesRepository.isQuickAccessTileTutorialRequired = false
        _uiState.update {
            it.copy(isQuickAccessTileTutorialRequired = false)
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
    val loginInfoCardData: LoginInfoCardData? = null,
    val userAuthenticationStatus: UserAuthenticationStatus = UserAuthenticationStatus.UN_AUTHENTICATED,
    val isQuickAccessTileTutorialRequired: Boolean = false,
    val quickAccessTileItems: List<QuickAccessTileItem> = emptyList()
)

data class LoginInfoCardData(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val buttonText: Int,
    @DrawableRes val image: Int = 0
)
