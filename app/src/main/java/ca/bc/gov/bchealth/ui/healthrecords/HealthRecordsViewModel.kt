package ca.bc.gov.bchealth.ui.healthrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.repository.HealthRecordsRepository
import ca.bc.gov.bchealth.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/*
* Created by amit_metri on 23,November,2021
*/
@HiltViewModel
class HealthRecordsViewModel @Inject constructor(
    val healthRecordsRepository: HealthRecordsRepository,
    val cardRepository: CardRepository
) : ViewModel() {

    /*
     * Used to manage Success, Error and Loading status in the UI
     * */
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = healthRecordsRepository.responseSharedFlow

    val healthRecordsSharedFlow: SharedFlow<List<HealthRecord>>
        get() = healthRecordsRepository.healthRecordsSharedFlow

    fun prepareHealthRecords() = viewModelScope.launch {
        healthRecordsRepository.prepareHealthRecords()
    }
}
