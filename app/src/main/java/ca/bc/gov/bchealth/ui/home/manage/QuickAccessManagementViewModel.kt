package ca.bc.gov.bchealth.ui.home.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.AppFeatureName
import ca.bc.gov.common.model.QuickAccessLinkName
import ca.bc.gov.common.model.QuickAccessTileShowAsQuickLinkDto
import ca.bc.gov.repository.settings.AppFeatureWithQuickAccessTilesRepository
import ca.bc.gov.repository.settings.QuickAccessTileRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickAccessManagementViewModel @Inject constructor(
    private val appFeatureRepository: AppFeatureWithQuickAccessTilesRepository,
    private val quickAccessTileRepository: QuickAccessTileRepository,
    private val mobileConfigRepository: MobileConfigRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickAccessManagementUiState())
    val uiState: StateFlow<QuickAccessManagementUiState> = _uiState.asStateFlow()

    fun loadQuickAccessTileData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        var featureWithQuickAccessItems = appFeatureRepository.getAppFeaturesWithQuickAccessTiles().filter { it.appFeatureDto.hasManageableQuickAccessLinks }
            .map {
                FeatureWithQuickAccessItems(
                    id = it.appFeatureDto.id,
                    name = it.appFeatureDto.name.value,
                    quickAccessItems = it.quickAccessTiles.map { tile ->
                        QuickAccessItem(
                            tile.id,
                            tile.tileName.value,
                            tile.showAsQuickAccess
                        )
                    }
                )
            }

        featureWithQuickAccessItems.forEach { feature ->
            val flags = mobileConfigRepository.getPatientDataSetFeatureFlags()
            if (feature.name == AppFeatureName.HEALTH_RECORDS.value) {
                val links = feature.quickAccessItems.toMutableList()

                if (!flags.isBcCancerScreeningEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.BC_CANCER_SCREENING.value }
                }
                if (!flags.isImmunizationEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.IMMUNIZATIONS.value }
                }
                if (!flags.isMedicationEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.MEDICATIONS.value }
                }
                if (!flags.isLabResultEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.LAB_RESULTS.value }
                }
                if (!flags.isCovid19TestResultEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.COVID_19_TESTS.value }
                }
                if (!flags.isHealthVisitEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.HEALTH_VISITS.value }
                }
                if (!flags.isSpecialAuthorityRequestEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.SPECIAL_AUTHORITY.value }
                }
                if (!flags.isClinicalDocumentEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.CLINICAL_DOCUMENTS.value }
                }
                if (!flags.isHospitalVisitEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.HOSPITAL_VISITS.value }
                }
                if (!flags.isDiagnosticImagingEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.IMAGING_REPORTS.value }
                }
                feature.quickAccessItems = links
            }

            if (feature.name == AppFeatureName.SERVICES.value) {
                val links = feature.quickAccessItems.toMutableList()
                val serviceFlag = mobileConfigRepository.getServicesFeatureFlag()
                if (!serviceFlag.isOrganDonorRegistrationEnabled()) {
                    links.removeIf { it.name == QuickAccessLinkName.ORGAN_DONOR.value }
                }
                feature.quickAccessItems = links
            }
        }
        _uiState.update { it.copy(featureWithQuickAccessItems = featureWithQuickAccessItems, isLoading = false) }
    }

    fun toggleItem(item: QuickAccessItem) {
        item.isEnabled = item.isEnabled.not()
    }

    fun saveSelection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            _uiState.value.featureWithQuickAccessItems.forEach {
                quickAccessTileRepository.updateAll(
                    it.quickAccessItems.map { tile ->
                        QuickAccessTileShowAsQuickLinkDto(tile.id, tile.isEnabled)
                    }
                )
            }

            delay(300L)

            _uiState.update { it.copy(isLoading = false, isUpdateCompleted = true) }
        }
    }

    data class QuickAccessManagementUiState(
        val isLoading: Boolean = false,
        val isUpdateCompleted: Boolean = false,
        val featureWithQuickAccessItems: List<FeatureWithQuickAccessItems> = emptyList()
    )

    data class FeatureWithQuickAccessItems(
        val id: Long = 0,
        val name: String,
        var quickAccessItems: List<QuickAccessItem> = emptyList()
    )

    data class QuickAccessItem(
        val id: Long = 0,
        val name: String,
        var isEnabled: Boolean = false
    )
}
