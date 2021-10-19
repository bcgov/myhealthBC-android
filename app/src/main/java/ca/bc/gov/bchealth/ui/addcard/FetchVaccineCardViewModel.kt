package ca.bc.gov.bchealth.ui.addcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.model.ImmunizationStatus
import ca.bc.gov.bchealth.model.network.responses.vaccinestatus.VaxStatusResponse
import ca.bc.gov.bchealth.repository.CardRepository
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.SHCDecoder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/*
* Created by amit_metri on 18,October,2021
*/
@HiltViewModel
class FetchVaccineCardViewModel @Inject constructor(
    private val shcDecoder: SHCDecoder,
    private val repository: CardRepository
) : ViewModel() {

    val vaxStatusResponseLiveData: LiveData<Response<VaxStatusResponse>>
        get() = repository.vaxStatusResponseLiveData

    suspend fun getVaccineStatus(phn: String, dob: String, dov: String) {
        repository.getVaccineStatus(phn, dob, dov)
    }

    val uploadStatus = MutableLiveData<Boolean>()

    fun processShcUri(
        shcUri: String
    ) = viewModelScope.launch {
        try {
            when (shcDecoder.getImmunizationStatus(shcUri).status) {
                ImmunizationStatus.FULLY_IMMUNIZED,
                ImmunizationStatus.PARTIALLY_IMMUNIZED -> {
                    saveCard(shcUri)
                    uploadStatus.value = true
                }

                ImmunizationStatus.INVALID_QR_CODE -> {
                    uploadStatus.value = false
                }
            }
        } catch (e: Exception) {
            uploadStatus.value = false
        }
    }

    private fun saveCard(uri: String) = viewModelScope.launch {
        repository.insert(HealthCard(uri = uri))
    }
}
