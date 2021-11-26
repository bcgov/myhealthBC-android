package ca.bc.gov.bchealth.ui.healthrecords.covidtestresults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.repository.HealthRecordsRepository
import ca.bc.gov.bchealth.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 24,November,2021
*/
@HiltViewModel
class FetchCovidTestResultViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val healthRecordsRepository: HealthRecordsRepository,
    private val dataStoreRepo: DataStoreRepo
) : ViewModel() {

    /*
     * Used to manage Success, Error and Loading status in the UI
     * */
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = healthRecordsRepository.responseSharedFlow

    val isRecentFormData = dataStoreRepo.isRecentFormData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    fun setRecentFormData(formData: String) = viewModelScope.launch {
        dataStoreRepo.setRecentFormData(formData)
    }

    fun getCovidTestResult(phn: String, dob: String, dot: Any) = viewModelScope.launch {
        healthRecordsRepository.getCovidTestResult(phn, dob, dot)
    }
}
