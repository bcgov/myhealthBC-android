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
            initializeAppFeatureAndQuickAccessTileData()
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

    private suspend fun initializeAppFeatureAndQuickAccessTileData() {
        val appFeatures = listOf(
            AppFeatureDto(
                featureNameId = R.string.health_records,
                featureIconId = R.drawable.icon_tile_health_record,
                destinationId = R.id.health_records,
                isEnabled = true,
                isQuickAccessEnabled = true,
            ),
            AppFeatureDto(
                featureNameId = R.string.immunization_schedules,
                featureIconId = R.drawable.ic_tile_immunization_schedules,
                destinationId = R.id.health_records,
                isEnabled = true,
                isQuickAccessEnabled = true,
            ),
            AppFeatureDto(
                featureNameId = R.string.health_resources,
                featureIconId = R.drawable.ic_tile_healt_resources,
                destinationId = R.id.action_homeFragment_to_resources,
                isEnabled = true,
                isQuickAccessEnabled = true,
            ),
            AppFeatureDto(
                featureNameId = R.string.health_passes,
                featureIconId = R.drawable.ic_tile_proof_of_vaccine,
                destinationId = R.id.action_homeFragment_to_health_pass,
                isEnabled = true,
                isQuickAccessEnabled = true,
            )
        )

        appFeatures.forEach {
            appFeatureRepository.insert(it)
        }
    }
}
