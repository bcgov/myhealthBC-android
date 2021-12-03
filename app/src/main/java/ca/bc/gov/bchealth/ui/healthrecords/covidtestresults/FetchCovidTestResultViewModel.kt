package ca.bc.gov.bchealth.ui.healthrecords.covidtestresults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.EncryptedPreferences
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.repository.HealthRecordsRepository
import ca.bc.gov.bchealth.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
* Created by amit_metri on 24,November,2021
*/
@HiltViewModel
class FetchCovidTestResultViewModel @Inject constructor(
    private val healthRecordsRepository: HealthRecordsRepository,
    private val encryptedPreferences: EncryptedPreferences
) : ViewModel() {

    /*
     * Used to manage Success, Error and Loading status in the UI
     * */
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = healthRecordsRepository.responseSharedFlow

    val isRecentFormData = encryptedPreferences.isRecentFormData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    fun setRecentFormData(formData: String) = viewModelScope.launch {
        encryptedPreferences.setRecentFormData(formData)
    }

    fun getCovidTestResult(phn: String, dob: String, dot: Any) = viewModelScope.launch {
        healthRecordsRepository.getCovidTestResult(phn, dob, dot)
    }

    /*
   * Used as an observable for healthRecords
   * */
    val healthRecordsSharedFlow: SharedFlow<List<HealthRecord>>
        get() = healthRecordsRepository.healthRecordsSharedFlow

    fun prepareHealthRecords() = viewModelScope.launch {
        healthRecordsRepository.prepareHealthRecords()
    }
}
