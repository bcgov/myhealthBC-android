package ca.bc.gov.bchealth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.BuildConfig.LOCAL_API_VERSION
import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.repository.OnBoardingRepository
import ca.bc.gov.repository.settings.AppFeatureRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val MAX_SPLASH_DELAY = 2000L

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val mobileConfigRepository: MobileConfigRepository,
    onBoardingRepository: OnBoardingRepository,
    private val appFeatureRepository: AppFeatureRepository
) : ViewModel() {

    private val _updateType: MutableLiveData<UpdateType> = MutableLiveData()
    val updateType: LiveData<UpdateType>
        get() = _updateType

    init {
        onBoardingRepository.checkIfReOnBoardingRequired(BuildConfig.VERSION_CODE)
        runBlocking {
            initializeAppFeaturesData()
        }
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

    enum class UpdateType {
        FORCE_UPDATE, CHECK_SOFT_UPDATE
    }

    private suspend fun initializeAppFeaturesData() {
        val appFeatures = listOf(
            AppFeatureDto(
                featureNameId = R.string.health_records,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.icon_tile_health_record,
                destinationId = R.id.health_records,
                isManagementEnabled = false,
                isQuickAccessEnabled = true,
            ),
            AppFeatureDto(
                featureNameId = R.string.immunization_schedules,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.ic_tile_immunization_schedules,
                destinationId = R.id.immunizationSchedulesFragment,
                isManagementEnabled = false,
                isQuickAccessEnabled = true,
            ),
            AppFeatureDto(
                featureNameId = R.string.health_resources,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.ic_tile_healt_resources,
                destinationId = R.id.action_homeFragment_to_resources,
                isManagementEnabled = false,
                isQuickAccessEnabled = true,
            ),
            AppFeatureDto(
                featureNameId = R.string.health_passes,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.ic_tile_proof_of_vaccine,
                destinationId = R.id.action_homeFragment_to_health_pass,
                isManagementEnabled = false,
                isQuickAccessEnabled = true,
            ),

            AppFeatureDto(
                featureNameId = R.string.feature_my_notes,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.icon_tile_health_record,
                destinationId = R.id.health_records,
                isManagementEnabled = true,
                isQuickAccessEnabled = false,
            ),

            AppFeatureDto(
                featureNameId = R.string.feature_immunization,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.ic_health_record_vaccine,
                destinationId = R.id.health_records,
                isManagementEnabled = true,
                isQuickAccessEnabled = false,
            ),

            AppFeatureDto(
                featureNameId = R.string.feature_medications,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.ic_health_record_medication,
                destinationId = R.id.health_records,
                isManagementEnabled = true,
                isQuickAccessEnabled = false,
            ),

            AppFeatureDto(
                featureNameId = R.string.feature_lab_results,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.ic_lab_test,
                destinationId = R.id.health_records,
                isManagementEnabled = true,
                isQuickAccessEnabled = false,
            ),

            AppFeatureDto(
                featureNameId = R.string.feature_special_authority,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.ic_health_record_special_authority,
                destinationId = R.id.health_records,
                isManagementEnabled = true,
                isQuickAccessEnabled = false,
            ),

            AppFeatureDto(
                featureNameId = R.string.feature_health_visit,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.ic_health_record_health_visit,
                destinationId = R.id.health_records,
                isManagementEnabled = true,
                isQuickAccessEnabled = false,
            ),

            AppFeatureDto(
                featureNameId = R.string.feature_clinic_documents,
                categoryId = R.string.feature_category_health_record,
                featureIconId = R.drawable.ic_health_record_clinical_document,
                destinationId = R.id.health_records,
                isManagementEnabled = true,
                isQuickAccessEnabled = false,
            ),

            AppFeatureDto(
                featureNameId = R.string.feature_organ_donor,
                categoryId = R.string.feature_category_service,
                featureIconId = R.drawable.ic_organ_donor,
                destinationId = R.id.health_records,
                isManagementEnabled = true,
                isQuickAccessEnabled = false,
            ),
        )

        appFeatureRepository.insert(appFeatures)
    }
}
