package ca.bc.gov.bchealth.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun setOnBoardingRequired(shown: Boolean) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            onBoardingRepository.setOnBoardingRequired(shown)
        }
    }

    fun setAppVersionCode(appVersionCode: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            onBoardingRepository.setAppVersionCode(appVersionCode)
        }
    }
}
