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
import ca.bc.gov.bchealth.workers.WorkerInvoker
import ca.bc.gov.common.model.AppFeatureName
import ca.bc.gov.common.model.QuickAccessLinkName
import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.common.model.settings.QuickAccessTileDto
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
    private val bannerRepository: BannerRepository,
    private val workerInvoker: WorkerInvoker,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(HomeComposeUiState(isQuickAccessTileTutorialRequired = appFeatureWithQuickAccessTilesRepository.isQuickAccessTileTutorialRequired))
    val uiState: StateFlow<HomeComposeUiState> = _uiState.asStateFlow()

    private var isBiometricAuthenticationRequired: Boolean = true

    fun loadQuickAccessTiles(loginStatus: LoginStatus) = viewModelScope.launch {
        val quickAccessTileItems = mutableListOf<QuickAccessTileItem>()
        val data = appFeatureWithQuickAccessTilesRepository.getAppFeaturesWithQuickAccessTiles()

        val appFeatures =
            appFeatureWithQuickAccessTilesRepository.getAppFeaturesWithQuickAccessTiles()
                .filter { it.appFeatureDto.showAsQuickAccess }
                .map {
                    QuickAccessTileItem.FeatureTileItem.from(it.appFeatureDto)
                }

        quickAccessTileItems.addAll(appFeatures)

        if (loginStatus == LoginStatus.ACTIVE) {
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
            (it.name == AppFeatureName.RECOMMENDED_IMMUNIZATIONS.value && (loginStatus != LoginStatus.ACTIVE))
        }

        _uiState.update { it.copy(isLoading = false, quickAccessTileItems = quickAccessTileItems) }
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

    fun executeOneTimeDataFetch() = workerInvoker.executeOneTimeDataFetch()
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

sealed class QuickAccessTileItem(
    @DrawableRes open val icon: Int,
    open val name: String,
    open val payload: String? = null,
    @IdRes open val destinationId: Int,
    open val isEditable: Boolean = false
) {
    data class FeatureTileItem(
        val id: Long,
        override val icon: Int,
        override val name: String,
        override val payload: String?,
        override val destinationId: Int,
        override val isEditable: Boolean
    ) : QuickAccessTileItem(
        icon, name, payload, destinationId, isEditable
    ) {
        companion object {
            fun from(appFeatureDto: AppFeatureDto): FeatureTileItem {
                val (tileIcon, endDestinationId) = when (appFeatureDto.name) {
                    AppFeatureName.HEALTH_RECORDS -> {
                        Pair(R.drawable.icon_tile_health_record, R.id.health_records)
                    }

                    AppFeatureName.IMMUNIZATION_SCHEDULES -> {

                        Pair(
                            R.drawable.ic_tile_immunization_schedules,
                            R.id.immunizationSchedulesFragment
                        )
                    }

                    AppFeatureName.HEALTH_RESOURCES -> {
                        Pair(
                            R.drawable.ic_tile_healt_resources,
                            R.id.action_homeFragment_to_resources
                        )
                    }

                    AppFeatureName.PROOF_OF_VACCINE -> {
                        Pair(
                            R.drawable.ic_tile_proof_of_vaccine,
                            R.id.action_homeFragment_to_health_pass
                        )
                    }

                    AppFeatureName.SERVICES -> {
                        Pair(R.drawable.ic_organ_donor, R.id.services)
                    }

                    AppFeatureName.RECOMMENDED_IMMUNIZATIONS -> {
                        Pair(R.drawable.ic_recommendation_immunization, R.id.recommendations)
                    }
                }
                return FeatureTileItem(
                    id = appFeatureDto.id,
                    name = appFeatureDto.name.value,
                    icon = tileIcon,
                    destinationId = endDestinationId,
                    payload = null,
                    isEditable = false
                )
            }
        }
    }

    data class QuickLinkTileItem(
        val id: Long,
        val featureId: Long,
        override val icon: Int,
        override val name: String,
        override val payload: String?,
        override val destinationId: Int,
        override val isEditable: Boolean
    ) : QuickAccessTileItem(
        icon, name, payload, destinationId, isEditable
    ) {
        companion object {
            fun from(quickAccessTileDto: QuickAccessTileDto): QuickLinkTileItem {
                val (tileIcon, endDestination) = when (quickAccessTileDto.tileName) {
                    QuickAccessLinkName.IMMUNIZATIONS -> {
                        Pair(R.drawable.ic_health_record_vaccine, R.id.health_records)
                    }

                    QuickAccessLinkName.MEDICATIONS -> {
                        Pair(R.drawable.ic_health_record_medication, R.id.health_records)
                    }

                    QuickAccessLinkName.LAB_RESULTS -> {
                        Pair(R.drawable.ic_lab_test, R.id.health_records)
                    }

                    QuickAccessLinkName.COVID_19_TESTS -> {
                        Pair(R.drawable.ic_health_record_covid_test, R.id.health_records)
                    }

                    QuickAccessLinkName.HEALTH_VISITS -> {
                        Pair(R.drawable.ic_health_record_health_visit, R.id.health_records)
                    }

                    QuickAccessLinkName.MY_NOTES -> {
                        Pair(R.drawable.ic_health_record_vaccine, R.id.health_records)
                    }

                    QuickAccessLinkName.SPECIAL_AUTHORITY -> {
                        Pair(R.drawable.ic_health_record_special_authority, R.id.health_records)
                    }

                    QuickAccessLinkName.CLINICAL_DOCUMENTS -> {
                        Pair(R.drawable.ic_health_record_clinical_document, R.id.health_records)
                    }

                    QuickAccessLinkName.HOSPITAL_VISITS -> {
                        Pair(R.drawable.ic_health_record_hospital_visit, R.id.health_records)
                    }

                    QuickAccessLinkName.IMAGING_REPORTS -> {
                        Pair(R.drawable.ic_health_record_diagnostic_imaging, R.id.health_records)
                    }

                    QuickAccessLinkName.ORGAN_DONOR -> {
                        Pair(R.drawable.ic_organ_donor, R.id.services)
                    }
                }
                return QuickLinkTileItem(
                    id = quickAccessTileDto.id,
                    featureId = quickAccessTileDto.featureId,
                    name = quickAccessTileDto.tileName.value,
                    payload = quickAccessTileDto.tilePayload,
                    icon = tileIcon,
                    destinationId = endDestination,
                    isEditable = true
                )
            }
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
