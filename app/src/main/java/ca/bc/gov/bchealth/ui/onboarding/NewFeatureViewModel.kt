package ca.bc.gov.bchealth.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 02,November,2021
*/
@HiltViewModel
class NewFeatureViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepo
) : ViewModel() {

    fun setNewFeatureShown(shown: Boolean) = viewModelScope.launch {
        dataStoreRepo.setNewFeatureShown(shown)
    }
}
