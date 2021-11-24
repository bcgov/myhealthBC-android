package ca.bc.gov.bchealth.ui.healthrecords.covidtestresults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/*
* Created by amit_metri on 24,November,2021
*/
@HiltViewModel
class FetchCovidTestResultViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepo
) : ViewModel() {

    val isRecentFormData = dataStoreRepo.isRecentFormData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )
}
