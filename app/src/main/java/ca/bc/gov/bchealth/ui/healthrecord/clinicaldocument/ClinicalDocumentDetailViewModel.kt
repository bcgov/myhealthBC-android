package ca.bc.gov.bchealth.ui.healthrecord.clinicaldocument

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ClinicalDocumentDetailViewModel @Inject constructor(
) : ViewModel() {
    private val _uiState = MutableStateFlow(ClinicalDocumentUiState())
    val uiState: StateFlow<ClinicalDocumentUiState> = _uiState.asStateFlow()

    fun getClinicalDocumentDetails(clinicalDocumentId: Long) = viewModelScope.launch {
        try {
            val dto: ClinicalDocumentDto = getSampleClinicalDocument(clinicalDocumentId)

            val uiList: List<ClinicalDocumentDetailItem> = listOf(
                ClinicalDocumentDetailItem(
                    R.string.clinical_documents_detail_discipline,
                    dto.discipline,
                ),

                ClinicalDocumentDetailItem(
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
}

private fun getSampleClinicalDocument(id: Long) = ClinicalDocumentDto(
    name = "Discharge Summary",
    type = "type",
    facilityName = "BC Women's Hospital",
    discipline = "Inpatient",
    serviceDate = Instant.now(),
    fileId = "fileId",
)

data class ClinicalDocumentUiState(
    val onLoading: Boolean = false,
    val toolbarTitle: String? = "",
    val uiList: List<ClinicalDocumentDetailItem> = emptyList()
)

data class ClinicalDocumentDetailItem(
    val title: Int,
    val description: String,
)
