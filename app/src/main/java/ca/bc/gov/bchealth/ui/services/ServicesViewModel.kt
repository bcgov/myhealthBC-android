package ca.bc.gov.bchealth.ui.services

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.services.OrganDonationDto
import ca.bc.gov.common.model.services.OrganDonorStatusDto
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.services.OrganDonorRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
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
    private val organDonorRepository: OrganDonorRepository,
    private val mobileConfigRepository: MobileConfigRepository,
    private val bcscAuthRepo: BcscAuthRepo
) : BaseViewModel() {

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
                    organDonationRegistrationDetail = organDonation.toUIModel(),
                    organDonorFileStatus = OrganDonorFileStatus.REQUIRE_DOWNLOAD
                )
            }
        } catch (e: Exception) {
            when (e) {
                is MyHealthException -> {
                    _uiState.update {
                        it.copy(
                            onLoading = false,
                            organDonationRegistrationDetail = OrganDonationRegistrationDetail(status = OrganDonorStatusDto.ERROR),
                            organDonorFileStatus = OrganDonorFileStatus.ERROR
                        )
                    }
                }
            }
        }
    }

    fun getPatientFile(fileId: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                onLoading = false,
                organDonorFileStatus = OrganDonorFileStatus.DOWNLOAD_IN_PROGRESS
            )
        }
        try {
            val patient =
                patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
            val organDonorDetails = organDonorRepository.findOrganDonationStatusById(patient.id)
            if (organDonorDetails.file.isNullOrBlank()) {
                val authParams = bcscAuthRepo.getAuthParametersDto()
                mobileConfigRepository.refreshMobileConfiguration()
                val fileString =
                    organDonorRepository.fetchPatientFile(authParams.hdid, authParams.token, fileId)
                organDonorDetails.file = fileString
                organDonorRepository.update(organDonorDetails)
            }
            _uiState.update {
                it.copy(
                    onLoading = false,
                    organDonationRegistrationDetail = organDonorDetails.toUIModel(),
                    organDonorFileStatus = OrganDonorFileStatus.DOWNLOADED
                )
            }
        } catch (e: Exception) {
            handleBaseException(e) { exception ->
            }
            _uiState.update {
                it.copy(
                    onLoading = false,
                    organDonorFileStatus = OrganDonorFileStatus.ERROR
                )
            }
        }
    }

    fun showProgressBar() {
        _uiState.update {
            it.copy(
                onLoading = true,
                organDonationRegistrationDetail = null,
                organDonorFileStatus = OrganDonorFileStatus.REQUIRE_DOWNLOAD
            )
        }
    }

    fun onPdfViewed() {
        _uiState.update {
            it.copy(
                onLoading = false,
                organDonorFileStatus = OrganDonorFileStatus.REQUIRE_DOWNLOAD
            )
        }
    }
}

data class ServicesUiState(
    val onLoading: Boolean = false,
    val organDonationRegistrationDetail: OrganDonationRegistrationDetail? = null,
    val organDonorFileStatus: OrganDonorFileStatus = OrganDonorFileStatus.REQUIRE_DOWNLOAD
)

enum class OrganDonorFileStatus {
    DOWNLOADED,
    REQUIRE_DOWNLOAD,
    DOWNLOAD_IN_PROGRESS,
    ERROR
}

data class OrganDonationRegistrationDetail(
    val status: OrganDonorStatusDto = OrganDonorStatusDto.NOT_REGISTERED,
    val statusMessage: String? = null,
    val fileId: String? = null,
    val file: String? = null
)

private fun OrganDonationDto.toUIModel() = OrganDonationRegistrationDetail(
    status = status,
    statusMessage = statusMessage,
    fileId = registrationFileId,
    file = file
)
