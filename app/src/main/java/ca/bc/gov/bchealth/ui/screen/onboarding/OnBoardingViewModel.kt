package ca.bc.gov.bchealth.ui.screen.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.OnBoardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author pinakin.kansara
 * Created 2023-10-27 at 10:55 a.m.
 */
@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val onBoardingRepository: OnBoardingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnBoardingUIState())
    val uiState: StateFlow<OnBoardingUIState> = _uiState.asStateFlow()

    init {
        val list = if (onBoardingRepository.isReOnBoardingRequired) {
            getReOnBoardingSliderItems()
        } else {
            getFirstTimeOnBoardingSliderItems()
        }
        _uiState.update { it.copy(pageCount = list.size, onBoardingSliderItems = list, isExistingUser = onBoardingRepository.isReOnBoardingRequired) }
    }

    private fun getFirstTimeOnBoardingSliderItems() = listOf<OnBoardingSliderItem>(
        OnBoardingSliderItem(
            titleResId = R.string.onboarding_health_records_title,
            descriptionResId = R.string.onboarding_health_records_desc,
            iconResId = R.drawable.ic_onboarding_health_records
        ),
        OnBoardingSliderItem(
            titleResId = R.string.onboarding_dependents_title,
            descriptionResId = R.string.onboarding_dependents_desc,
            iconResId = R.drawable.ic_onboarding_dependent
        ),
        OnBoardingSliderItem(
            titleResId = R.string.onboarding_health_passes_title,
            descriptionResId = R.string.onboarding_health_passes_desc,
            iconResId = R.drawable.ic_onboarding_health_passes
        ),

        OnBoardingSliderItem(
            titleResId = R.string.onboarding_health_resources_title,
            descriptionResId = R.string.onboarding_health_resources_desc,
            iconResId = R.drawable.ic_onboarding_health_recources
        ),

        OnBoardingSliderItem(
            titleResId = R.string.onboarding_services_title,
            descriptionResId = R.string.onboarding_services_desc,
            iconResId = R.drawable.ic_onboarding_services
        )
    )

    private fun getReOnBoardingSliderItems() = listOf<OnBoardingSliderItem>(
        OnBoardingSliderItem(
            titleResId = R.string.onboarding_services_title,
            descriptionResId = R.string.onboarding_services_desc,
            iconResId = R.drawable.ic_onboarding_services
        )
    )

    fun setOnBoardingRequired(isRequired: Boolean) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            onBoardingRepository.onBoardingRequired = isRequired
            onBoardingRepository.isReOnBoardingRequired = isRequired
            onBoardingRepository.previousOnBoardingScreenName = BuildConfig.FLAG_NEW_ON_BOARDING_SCREEN
        }
    }

    fun setAppVersionCode(versionCode: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            onBoardingRepository.previousVersionCode = versionCode
        }
    }
}

data class OnBoardingUIState(
    val pageCount: Int = 0,
    val onBoardingSliderItems: List<OnBoardingSliderItem> = emptyList(),
    val isExistingUser: Boolean = false
)

data class OnBoardingSliderItem(
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    @DrawableRes val iconResId: Int
)
