package ca.bc.gov.bchealth.ui.healthrecord.imaging

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordDetailItem
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.services.DiagnosticImagingRepository
import ca.bc.gov.repository.services.PatientServicesRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticImagingDetailViewModel @Inject constructor(
    private val mobileConfigRepository: MobileConfigRepository,
    private val diagnosticImagingRepository: DiagnosticImagingRepository,
    private val patientServicesRepository: PatientServicesRepository,
    private val bcscAuthRepo: BcscAuthRepo
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(DiagnosticImagingDataDetailUiState())
    val uiState: StateFlow<DiagnosticImagingDataDetailUiState> = _uiState.asStateFlow()

    fun getDiagnosticImagingDataDetails(id: Long) = viewModelScope.launch {
        try {
            val data = diagnosticImagingRepository.getDiagnosticImagingDataDetails(id)

            val details = listOf<HealthRecordDetailItem>(

                HealthRecordDetailItem(
                    title = R.string.health_authority,
                    description = data.healthAuthority ?: "--"
                ),
            )

            _uiState.update {
                it.copy(
                    onLoading = false,
                    toolbarTitle = data.modality ?: "",
                    details = details,
                    fileId = data.fileId,
                    id = data.id
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    onLoading = false
                )
            }
        }
    }

    fun onClickDownload() = viewModelScope.launch {
        _uiState.update { it.copy(onLoading = true) }

        try {
            mobileConfigRepository.refreshMobileConfiguration()
            val authParams = bcscAuthRepo.getAuthParametersDto()
            mobileConfigRepository.refreshMobileConfiguration()
            _uiState.value.fileId?.let { id ->
                val fileString =
                    patientServicesRepository.fetchPatientDataFile(authParams.hdid, id)

                _uiState.update { it.copy(pdfData = fileString, onLoading = false) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(onLoading = false) }
            handleBaseException(e)
        }
    }

    fun resetPdfState() {
        _uiState.update { it.copy(pdfData = null) }
    }
}

data class DiagnosticImagingDataDetailUiState(
    val onLoading: Boolean = false,
    val toolbarTitle: String = "",
    val details: List<HealthRecordDetailItem> = emptyList(),
    val fileId: String? = null,
    val pdfData: String? = null,
    val id: String? = null
)
