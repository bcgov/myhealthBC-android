package ca.bc.gov.bchealth.ui.healthrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.repository.HealthRecordsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 25,November,2021
*/
@HiltViewModel
class IndividualHealthRecordViewModel @Inject constructor(
    val healthRecordsRepository: HealthRecordsRepository,
    val dataSource: LocalDataSource
) : ViewModel() {

    companion object {
        const val bulletPoint = " \u2022 "
    }

    val healthRecords = healthRecordsRepository.individualHealthRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun deleteCovidTestResult(reportId: String) = viewModelScope.launch {
        dataSource.deleteCovidTestResult(reportId)
    }

    fun deleteVaccineRecord(healthPassId: Int) = viewModelScope.launch {
        dataSource.deleteVaccineData(healthPassId)
    }
}
