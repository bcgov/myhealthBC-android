package ca.bc.gov.bchealth.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import ca.bc.gov.bchealth.datasource.LocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 16,November,2021
*/
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepo,
    private val localDataSource: LocalDataSource
) : ViewModel() {

    fun trackAnalytics(value: Boolean) = viewModelScope.launch {
        dataStoreRepo.trackAnalytics(value)
    }

    fun deleteAllRecordsAndSavedData() = viewModelScope.launch {
        localDataSource.deleteAllRecords()
        dataStoreRepo.setRecentFormData("")
    }
}
