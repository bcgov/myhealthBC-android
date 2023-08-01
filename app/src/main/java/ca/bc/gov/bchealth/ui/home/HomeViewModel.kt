package ca.bc.gov.bchealth.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.COMMUNICATION_BANNER_MAX_LENGTH
import ca.bc.gov.bchealth.utils.fromHtml
import ca.bc.gov.common.model.AppFeatureName
import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.repository.BannerRepository
import ca.bc.gov.repository.OnBoardingRepository
import ca.bc.gov.repository.settings.AppFeatureWithQuickAccessTilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appFeatureWithQuickAccessTilesRepository: AppFeatureWithQuickAccessTilesRepository,
    private val onBoardingRepository: OnBoardingRepository,
    private val bannerRepository: BannerRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(HomeComposeUiState(isQuickAccessTileTutorialRequired = appFeatureWithQuickAccessTilesRepository.isQuickAccessTileTutorialRequired))
    val uiState: StateFlow<HomeComposeUiState> = _uiState.asStateFlow()

    private var isBiometricAuthenticationRequired: Boolean = true

    fun loadQuickAccessTiles(loginStatus: LoginStatus) = viewModelScope.launch {
        val tiles = if (loginStatus == LoginStatus.ACTIVE) {
            appFeatureWithQuickAccessTilesRepository.getQuickAccessFeatures()
        } else {
            appFeatureWithQuickAccessTilesRepository.getNonManageableAppFeatures()
        }

        val appFeatures = tiles.map {
            QuickAccessTileItem.from(it)
        }

        _uiState.update { it.copy(quickAccessTileItems = appFeatures) }
    }

    fun launchCheck() = viewModelScope.launch {
        when {
            onBoardingRepository.onBoardingRequired -> {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        launchCheckStatus = LaunchCheckStatus.REQUIRE_ON_BOARDING
                    )
                }
            }

            onBoardingRepository.isReOnBoardingRequired -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        launchCheckStatus = LaunchCheckStatus.REQUIRE_RE_ON_BOARDING
                    )
                }
            }

            isBiometricAuthenticationRequired -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        launchCheckStatus = LaunchCheckStatus.REQUIRE_BIOMETRIC_AUTHENTICATION
                    )
                }
            }

            else -> {
                _uiState.update {
                    it.copy(isLoading = false, launchCheckStatus = LaunchCheckStatus.SUCCESS)
                }
            }
        }
    }

    fun onBiometricAuthenticationCompleted() {
        isBiometricAuthenticationRequired = false
        resetUIState()
        launchCheck()
    }

    fun resetUIState() {
        _uiState.update {
            it.copy(launchCheckStatus = null)
        }
    }

    fun fetchBanner() = viewModelScope.launch {
        try {
            bannerRepository.getBanner()?.let { banner ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
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

    fun dismissBanner() {
        _uiState.update {
            it.copy(isLoading = false, bannerItem = it.bannerItem?.copy(isDismissed = true))
        }
    }

    fun getLoginInfoCardData(loginStatus: LoginStatus): LoginInfoCardData? {

        return when (loginStatus) {

            LoginStatus.ACTIVE -> {
                null
            }

            LoginStatus.EXPIRED -> {
                LoginInfoCardData(
                    title = R.string.session_time_out,
                    description = R.string.login_to_view_hidden_records_msg,
                    buttonText = R.string.log_in
                )
            }

            LoginStatus.NOT_AUTHENTICATED -> {
                LoginInfoCardData(
                    title = R.string.log_in_with_bc_services_card,
                    description = R.string.log_in_description,
                    buttonText = R.string.get_started,
                    image = R.drawable.img_un_authenticated_home_screen
                )
            }
        }
    }

    fun tutorialDismissed() {
        appFeatureWithQuickAccessTilesRepository.isQuickAccessTileTutorialRequired = false
        _uiState.update {
            it.copy(isQuickAccessTileTutorialRequired = false)
        }
    }
}

data class HomeComposeUiState(
    val isLoading: Boolean = false,
    val launchCheckStatus: LaunchCheckStatus? = null,
    val bannerItem: HomeBannerItem? = null,
    val loginInfoCardData: LoginInfoCardData? = null,
    val quickAccessTileItems: List<QuickAccessTileItem> = emptyList(),
    val isQuickAccessTileTutorialRequired: Boolean = false
)

enum class LaunchCheckStatus {
    REQUIRE_ON_BOARDING,
    REQUIRE_RE_ON_BOARDING,
    REQUIRE_BIOMETRIC_AUTHENTICATION,
    SUCCESS
}

data class LoginInfoCardData(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val buttonText: Int,
    @DrawableRes val image: Int = 0
)

data class QuickAccessTileItem(
    @DrawableRes val icon: Int,
    val name: String,
    val payload: String? = null,
    @IdRes val destinationId: Int,
    val isEditable: Boolean = false,
    var isQuickAccess: Boolean = false,
    @StringRes val category: Int,
) {
    companion object {
        fun from(appFeatureDto: AppFeatureDto): QuickAccessTileItem {

            val (tileIcon, endDestinationId, category) = when (appFeatureDto.name) {

                AppFeatureName.HEALTH_RECORDS -> Triple(
                    R.drawable.icon_tile_health_record,
                    R.id.health_records,
                    R.string.feature_category_health_record
                )

                AppFeatureName.IMMUNIZATION_SCHEDULES -> Triple(
                    R.drawable.ic_tile_immunization_schedules,
                    R.id.immunizationSchedulesFragment, R.string.feature_category_health_record
                )

                AppFeatureName.HEALTH_RESOURCES -> Triple(
                    R.drawable.ic_tile_healt_resources,
                    R.id.action_homeFragment_to_resources, R.string.feature_category_health_record
                )

                AppFeatureName.PROOF_OF_VACCINE -> Triple(
                    R.drawable.ic_tile_proof_of_vaccine,
                    R.id.action_homeFragment_to_health_pass, R.string.feature_category_health_record
                )

                AppFeatureName.SERVICES -> Triple(
                    R.drawable.ic_organ_donor,
                    R.id.services, R.string.feature_category_service
                )

                AppFeatureName.IMMUNIZATIONS -> Triple(
                    R.drawable.ic_health_record_vaccine,
                    R.id.health_records, R.string.feature_category_health_record
                )

                AppFeatureName.MEDICATIONS -> Triple(
                    R.drawable.ic_health_record_medication,
                    R.id.health_records, R.string.feature_category_health_record
                )

                AppFeatureName.COVID_TESTS -> Triple(
                    R.drawable.ic_health_record_covid_test,
                    R.id.health_records, R.string.feature_category_health_record
                )

                AppFeatureName.IMAGING_REPORTS -> Triple(
                    R.drawable.ic_health_record_diagnostic_imaging,
                    R.id.health_records, R.string.feature_category_health_record
                )

                AppFeatureName.HOSPITAL_VISITS -> Triple(
                    R.drawable.ic_health_record_hospital_visit,
                    R.id.health_records, R.string.feature_category_health_record
                )

                AppFeatureName.MY_NOTES -> Triple(
                    R.drawable.icon_tile_health_record,
                    R.id.health_records, R.string.feature_category_health_record
                )

                AppFeatureName.LAB_RESULTS -> Triple(
                    R.drawable.ic_lab_test,
                    R.id.health_records, R.string.feature_category_health_record
                )

                AppFeatureName.SPECIAL_AUTHORITY -> Triple(
                    R.drawable.ic_health_record_special_authority,
                    R.id.health_records, R.string.feature_category_health_record
                )

                AppFeatureName.HEALTH_VISITS -> Triple(
                    R.drawable.ic_health_record_health_visit,
                    R.id.health_records, R.string.feature_category_health_record
                )

                AppFeatureName.CLINICAL_DOCUMENTS -> Triple(
                    R.drawable.ic_health_record_clinical_document,
                    R.id.health_records, R.string.feature_category_health_record
                )
            }

            return QuickAccessTileItem(
                name = appFeatureDto.name.value,
                icon = tileIcon,
                destinationId = endDestinationId,
                payload = appFeatureDto.name.value,
                category = category,
                isEditable = appFeatureDto.hasManageableQuickAccessLinks,
                isQuickAccess = appFeatureDto.showAsQuickAccess
            )
        }
    }
}

data class HomeBannerItem(
    val title: String,
    val body: String,
    var isDismissed: Boolean = false
) {
    fun showReadMore() = body.fromHtml().length > COMMUNICATION_BANNER_MAX_LENGTH
}
