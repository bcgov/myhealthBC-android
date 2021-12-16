package ca.bc.gov.bchealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import ca.bc.gov.bchealth.model.ImmunizationRecord
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.repository.HealthRecordsRepository
import ca.bc.gov.bchealth.repository.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 18,October,2021
*/
@HiltViewModel
class FetchVaccineDataViewModel @Inject constructor(
    private val repository: CardRepository,
    private val dataStoreRepo: DataStoreRepo,
    private val healthRecordsRepository: HealthRecordsRepository
) : ViewModel() {

    /*
     * Used to manage Success, Error and Loading status in the UI
     * */
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = repository.responseSharedFlow

    suspend fun getVaccineStatus(phn: String, dob: String, dov: String) {
        repository.getVaccineStatus(phn, dob, dov)
    }

    fun setRecentFormData(formData: String) = viewModelScope.launch {
        dataStoreRepo.setRecentFormData(formData)
    }

    val isRecentFormData = dataStoreRepo.isRecentFormData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    fun replaceExitingHealthPass(healthCard: HealthCard) = viewModelScope.launch {
        repository.replaceExitingHealthPass(healthCard)
    }

    val healthRecords = healthRecordsRepository.healthRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun fetchHealthRecordFromHealthCard(healthCard: HealthCard): ImmunizationRecord? {
        return healthRecordsRepository.fetchHealthRecordFromHealthCard(healthCard)
    }
}
