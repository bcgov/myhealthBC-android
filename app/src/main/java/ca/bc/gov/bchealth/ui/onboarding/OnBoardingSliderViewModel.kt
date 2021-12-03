package ca.bc.gov.bchealth.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.EncryptedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/*
* Created by amit_metri on 13,October,2021
*/
@HiltViewModel
class OnBoardingSliderViewModel @Inject constructor(
    private val encryptedPreferences: EncryptedPreferences
) : ViewModel() {

    fun setOnBoardingShown(shown: Boolean) = viewModelScope.launch {
        encryptedPreferences.setOnBoardingShown(shown)
    }

    fun setNewFeatureShown(shown: Boolean) = viewModelScope.launch {
        encryptedPreferences.setNewFeatureShown(shown)
    }
}
