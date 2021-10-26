package ca.bc.gov.bchealth.ui.addcard

import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow

/*
* Created by amit_metri on 18,October,2021
*/
@HiltViewModel
class FetchVaccineCardViewModel @Inject constructor(
    private val repository: CardRepository
) : ViewModel() {

    /*
     * Used to manage Success, Error and Loading status in the UI
     * */
    val responseSharedFlow: SharedFlow<Response<String>>
        get() = repository.responseSharedFlow

    suspend fun getVaccineStatus(phn: String, dob: String, dov: String) {
        repository.getVaccineStatus(phn, dob, dov)
    }
}
