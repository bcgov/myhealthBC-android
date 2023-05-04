package ca.bc.gov.bchealth.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.OnBoardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*
* Created by amit_metri on 13,October,2021
*/
@HiltViewModel
class OnBoardingSliderViewModel @Inject constructor(
    private val onBoardingRepository: OnBoardingRepository
) : ViewModel() {

    val isReOnBoardingRequired = onBoardingRepository.isReOnBoardingRequired

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
