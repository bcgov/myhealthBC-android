package ca.bc.gov.bchealth.ui.healthrecord.clinicaldocument

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordDetailItem
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.repository.clinicaldocument.ClinicalDocumentRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClinicalDocumentDetailViewModel @Inject constructor(
    private val repository: ClinicalDocumentRepository,
    private val mobileConfigRepository: MobileConfigRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(ClinicalDocumentUiState())
    val uiState: StateFlow<ClinicalDocumentUiState> = _uiState.asStateFlow()
    private var fileId: String? = null

    fun getClinicalDocumentDetails(clinicalDocumentId: Long) = viewModelScope.launch {
        try {
            val dto: ClinicalDocumentDto = repository.getClinicalDocument(clinicalDocumentId)

            fileId = dto.fileId

            val uiList: List<HealthRecordDetailItem> = listOf(
                HealthRecordDetailItem(
                    R.string.clinical_documents_detail_discipline,
                    dto.discipline,
                ),

                HealthRecordDetailItem(
                    R.string.clinical_documents_detail_facility,
                    dto.facilityName,
                ),
            )

            _uiState.update { state ->
                state.copy(
                    onLoading = false,
                    toolbarTitle = dto.name,
                    uiList = uiList
                )
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(onLoading = false)
            }
        }
    }

    fun onClickDownload() = viewModelScope.launch {
        _uiState.update { it.copy(onLoading = true) }

        try {
            mobileConfigRepository.refreshMobileConfiguration()

            fileId?.apply {
                val pdfData = repository.fetchPdf(this)
                _uiState.update { it.copy(pdfData = pdfData, onLoading = false) }
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

data class ClinicalDocumentUiState(
    val onLoading: Boolean = false,
    val toolbarTitle: String? = "",
    val uiList: List<HealthRecordDetailItem> = emptyList(),
    val pdfData: String? = null
)
