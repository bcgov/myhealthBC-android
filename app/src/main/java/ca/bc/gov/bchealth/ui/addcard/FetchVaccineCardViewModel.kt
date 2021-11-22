package ca.bc.gov.bchealth.ui.addcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import ca.bc.gov.bchealth.repository.CardRepository
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
class FetchVaccineCardViewModel @Inject constructor(
    private val repository: CardRepository,
    private val dataStoreRepo: DataStoreRepo
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
}
