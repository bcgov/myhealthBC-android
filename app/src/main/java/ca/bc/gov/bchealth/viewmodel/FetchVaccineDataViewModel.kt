package ca.bc.gov.bchealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.EncryptedPreferences
import ca.bc.gov.bchealth.model.ImmunizationRecord
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.repository.HealthRecordsRepository
import ca.bc.gov.bchealth.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
* Created by amit_metri on 18,October,2021
*/
@HiltViewModel
class FetchVaccineDataViewModel @Inject constructor(
    private val repository: CardRepository,
    private val encryptedPreferences: EncryptedPreferences,
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
        encryptedPreferences.setRecentFormData(formData)
    }

    val isRecentFormData = encryptedPreferences.isRecentFormData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    fun replaceExitingHealthPass(healthCard: HealthCard) = viewModelScope.launch {
        repository.replaceExitingHealthPass(healthCard)
    }

    /*
    * Used as an observable for healthRecords
    * */
    val healthRecordsSharedFlow: SharedFlow<List<HealthRecord>>
        get() = healthRecordsRepository.healthRecordsSharedFlow

    fun prepareHealthRecords() = viewModelScope.launch {
        healthRecordsRepository.prepareHealthRecords()
    }

    fun fetchHealthRecordFromHealthCard(healthCard: HealthCard): ImmunizationRecord? {
        return healthRecordsRepository.fetchHealthRecordFromHealthCard(healthCard)
    }
}
