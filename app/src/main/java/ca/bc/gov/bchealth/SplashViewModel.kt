package ca.bc.gov.bchealth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.BuildConfig.LOCAL_API_VERSION
import ca.bc.gov.common.model.AppFeatureName
import ca.bc.gov.common.model.QuickAccessLinkName
import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.common.model.settings.QuickAccessTileDto
import ca.bc.gov.repository.OnBoardingRepository
import ca.bc.gov.repository.settings.AppFeatureRepository
import ca.bc.gov.repository.settings.QuickAccessTileRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MAX_SPLASH_DELAY = 2000L

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val mobileConfigRepository: MobileConfigRepository,
    onBoardingRepository: OnBoardingRepository,
    private val appFeatureRepository: AppFeatureRepository,
    private val quickAccessTileRepository: QuickAccessTileRepository
) : ViewModel() {

    private val _updateType: MutableLiveData<UpdateType> = MutableLiveData()
    val updateType: LiveData<UpdateType>
        get() = _updateType

    init {
        onBoardingRepository.checkIfReOnBoardingRequired(BuildConfig.VERSION_CODE)
        initializeAppData()
    }

    fun checkAppVersion() {
        viewModelScope.launch {
            try {
                val remoteVersion = mobileConfigRepository.getRemoteApiVersion()

                val updateType = if (LOCAL_API_VERSION < remoteVersion) {
                    UpdateType.FORCE_UPDATE
                } else {
                    UpdateType.CHECK_SOFT_UPDATE
                }

                async { delay(MAX_SPLASH_DELAY) }.await()

                _updateType.value = updateType
            } catch (e: Exception) {
                e.printStackTrace()
                _updateType.value = UpdateType.CHECK_SOFT_UPDATE
            }
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
            val immunization = QuickAccessTileDto(
                featureId = id,
                tileName = QuickAccessLinkName.IMMUNIZATIONS,
                tilePayload = "Immunization",
                showAsQuickAccess = true
            )
            quickAccessTileRepository.insert(immunization)
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
    }

    enum class UpdateType {
        FORCE_UPDATE, CHECK_SOFT_UPDATE
    }
}
