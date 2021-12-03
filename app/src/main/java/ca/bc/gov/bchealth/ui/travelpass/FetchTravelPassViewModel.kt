package ca.bc.gov.bchealth.ui.travelpass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.datasource.EncryptedPreferences
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
* Created by amit_metri on 11,November,2021
*/
@HiltViewModel
class FetchTravelPassViewModel @Inject constructor(
    private val repository: CardRepository,
    encryptedPreferences: EncryptedPreferences
) : ViewModel() {

    /*
     * Used to manage Success, Error and Loading status in the UI
     * */
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = repository.responseSharedFlow

    suspend fun getFederalTravelPass(healthCardDto: HealthCardDto, phn: String) {
        repository.getFederalTravelPass(healthCardDto, phn)
    }

    val isRecentFormData = encryptedPreferences.isRecentFormData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    fun replaceExitingHealthPass(healthCard: HealthCard) = viewModelScope.launch {
        try {
            repository.replaceExitingHealthPass(healthCard)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
