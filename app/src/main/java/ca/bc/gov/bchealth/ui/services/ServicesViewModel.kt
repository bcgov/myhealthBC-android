package ca.bc.gov.bchealth.ui.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.services.OrganDonationDto
import ca.bc.gov.common.model.services.OrganDonationStatus
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.services.OrganDonorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val organDonorRepository: OrganDonorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    fun getOrganDonationStatus() = viewModelScope.launch {
        try {
            val patient =
                patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
            val organDonation = organDonorRepository.findOrganDonationStatusById(patient.id)
            _uiState.update {
                it.copy(
                    onLoading = false,
                    organDonationRegistrationDetail = organDonation.toUIModel()
                )
            }
        } catch (e: Exception) {
            when (e) {
                is MyHealthException -> {
                    _uiState.update {
                        it.copy(
                            onLoading = false,
                            organDonationRegistrationDetail = OrganDonationRegistrationDetail(status = OrganDonationStatus.ERROR, statusMessage = e.message)
                        )
                    }
                }
            }
        }
    }

    fun showProgressBar() {
        _uiState.update { it.copy(onLoading = true, organDonationRegistrationDetail = null) }
    }
}

data class ServicesUiState(
    val onLoading: Boolean = false,
    val organDonationRegistrationDetail: OrganDonationRegistrationDetail? = null
)

data class OrganDonationRegistrationDetail(
    val status: OrganDonationStatus = OrganDonationStatus.NOT_REGISTERED,
    val statusMessage: String? = null,
    val file: String? = null
)

private fun OrganDonationDto.toUIModel() = OrganDonationRegistrationDetail(
    status = status,
    statusMessage = statusMessage,
    file = file
)
