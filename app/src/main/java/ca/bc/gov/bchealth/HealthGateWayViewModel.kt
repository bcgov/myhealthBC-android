package ca.bc.gov.bchealth

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.AppFeatureName
import ca.bc.gov.common.model.QuickAccessLinkName
import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.common.model.settings.QuickAccessTileDto
import ca.bc.gov.repository.settings.AppFeatureRepository
import ca.bc.gov.repository.settings.QuickAccessTileRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author pinakin.kansara
 * Created 2023-12-13 at 10:36 a.m.
 */
@HiltViewModel
class HealthGateWayViewModel @Inject constructor(
    private val mobileConfigRepository: MobileConfigRepository,
    private val appFeatureRepository: AppFeatureRepository,
    private val quickAccessTileRepository: QuickAccessTileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HealthGateWayUiState())
    val uiState: StateFlow<HealthGateWayUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "HealthGateWayViewModel"
    }
    init {
        syncConfig()
        initializeAppData()
    }

    private fun syncConfig() = viewModelScope.launch {
        Log.d(TAG, "synConfig() started")
        try {
            mobileConfigRepository.syncConfig()
        } catch (e: Exception) {
            Log.d(TAG, "syncConfig() exception = ${e.printStackTrace()}")
        } finally {
            _uiState.update { it.copy(keepSplashScreen = false) }
            Log.d(TAG, "syncConfig() completed")
        }
    }

    private fun initializeAppData() = viewModelScope.launch {
        val healthRecord = AppFeatureDto(
            name = AppFeatureName.HEALTH_RECORDS,
            hasManageableQuickAccessLinks = true,
            showAsQuickAccess = true
        )
        val id = appFeatureRepository.insert(healthRecord)

        if (id > 0) {

            val tiles = timelineQuickLinkTiles(id)
            quickAccessTileRepository.insertAll(tiles)
        }

        val immunizationSchedule = AppFeatureDto(
            name = AppFeatureName.IMMUNIZATION_SCHEDULES,
            hasManageableQuickAccessLinks = false,
            showAsQuickAccess = true
        )
        appFeatureRepository.insert(immunizationSchedule)

        val recommendations = AppFeatureDto(
            name = AppFeatureName.RECOMMENDED_IMMUNIZATIONS,
            hasManageableQuickAccessLinks = false,
            showAsQuickAccess = true
        )
        appFeatureRepository.insert(recommendations)

        val healthResources = AppFeatureDto(
            name = AppFeatureName.HEALTH_RESOURCES,
            hasManageableQuickAccessLinks = false,
            showAsQuickAccess = true
        )
        appFeatureRepository.insert(healthResources)

        val proofOfVaccine = AppFeatureDto(
            name = AppFeatureName.PROOF_OF_VACCINE,
            hasManageableQuickAccessLinks = false,
            showAsQuickAccess = true
        )
        appFeatureRepository.insert(proofOfVaccine)

        val services = AppFeatureDto(
            name = AppFeatureName.SERVICES,
            hasManageableQuickAccessLinks = true,
            showAsQuickAccess = false
        )

        val serviceId = appFeatureRepository.insert(services)
        if (serviceId > 0) {
            quickAccessTileRepository.insertAll(serviceQuickLinkTilesItem(serviceId))
        }
    }

    private fun serviceQuickLinkTilesItem(id: Long): List<QuickAccessTileDto> {
        return listOf(
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.ORGAN_DONOR,
                tilePayload = "Organ Donor",
                showAsQuickAccess = false
            )
        )
    }

    private fun timelineQuickLinkTiles(id: Long): List<QuickAccessTileDto> {
        return listOf(
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.IMMUNIZATIONS,
                tilePayload = "Immunization",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.MEDICATIONS,
                tilePayload = "Medications",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.LAB_RESULTS,
                tilePayload = "Laboratory",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.COVID_19_TESTS,
                tilePayload = "COVID19Laboratory",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.HEALTH_VISITS,
                tilePayload = "HealthVisit",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.SPECIAL_AUTHORITY,
                tilePayload = "SpecialAuthority",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.HOSPITAL_VISITS,
                tilePayload = "HospitalVisit",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.CLINICAL_DOCUMENTS,
                tilePayload = "ClinicalDocument",
                showAsQuickAccess = false
            ),
            QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.IMAGING_REPORTS,
                tilePayload = "ImagingReports",
                showAsQuickAccess = false
            )
        )
    }
}

@Stable
data class HealthGateWayUiState(
    val keepSplashScreen: Boolean = true
)