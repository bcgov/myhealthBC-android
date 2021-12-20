package ca.bc.gov.bchealth.ui.healthrecords.covidtestresults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.LocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 01,December,2021
*/
@HiltViewModel
class CovidTestResultViewModel @Inject constructor(
    private val dataSource: LocalDataSource
) : ViewModel() {

    fun deleteCovidTestResult(combinedReportId: String) = viewModelScope.launch {
        dataSource.deleteCovidTestResult(combinedReportId)
        delay(1000)
    }
}
