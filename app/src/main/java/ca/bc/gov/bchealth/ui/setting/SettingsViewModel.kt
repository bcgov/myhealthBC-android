package ca.bc.gov.bchealth.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.EncryptedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/*
* Created by amit_metri on 16,November,2021
*/
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val encryptedPreferences: EncryptedPreferences
) : ViewModel() {

    fun trackAnalytics(value: Boolean) = viewModelScope.launch {
        encryptedPreferences.trackAnalytics(value)
    }
}
