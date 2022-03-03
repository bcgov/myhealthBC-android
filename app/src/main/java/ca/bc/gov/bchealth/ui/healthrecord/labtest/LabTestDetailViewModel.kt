package ca.bc.gov.bchealth.ui.healthrecord.labtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LabTestDetailViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(LabTestDetailUiState())
    val uiState: StateFlow<LabTestDetailUiState> = _uiState.asStateFlow()
    
    fun getLabTestDetails() = viewModelScope.launch {
        try {
            _uiState.update {
                it.copy(onLoading = true)
            }

            _uiState.update {
                it.copy(
                    onLoading = false,
                    labTestDetails = prepareLabTestDetailsData(),
                    toolbarTitle = "Lab Test"
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    onError = true
                )
            }
        }
    }

    private fun prepareLabTestDetailsData(): List<LabTestDetail> {
        val labTestDetails = mutableListOf<LabTestDetail>()
        labTestDetails.add(
            LabTestDetail(
                title1 = "Collection date:",
                description1 = "Albumin",
                title2 = "Ordering provider",
                description2 = "100PLISBVCC, TREVOR",
                title3 = "Reporting Lab",
                description3 = "Vancouver Coastal Health"
            )
        )
        labTestDetails.add(
            LabTestDetail(
                header = "Test summary",
                title1 = "Test name:",
                description1 = "Albumin",
                title2 = "Result",
                description2 = "Out of Range",
                title3 = "Test Status",
                description3 = "Final",
                viewType = ITEM_VIEW_TYPE_HEADER
            )
        )
        labTestDetails.add(
            LabTestDetail(
                title1 = "Test name:",
                description1 = "Albumin",
                title2 = "Result",
                description2 = "Out of Range",
                title3 = "Test Status",
                description3 = "Final",
            )
        )
        labTestDetails.add(
            LabTestDetail(
                title1 = "Test name:",
                description1 = "Albumin",
                title2 = "Result",
                description2 = "Out of Range",
                title3 = "Test Status",
                description3 = "Final",
            )
        )

        return labTestDetails
    }
    
    companion object {
        const val ITEM_VIEW_TYPE_HEADER = 0
        const val ITEM_VIEW_TYPE_RECORD = 1
    }
}

data class LabTestDetailUiState(
    val onLoading: Boolean = false,
    val onError: Boolean = false,
    val labTestDetails: List<LabTestDetail>? = null,
    val toolbarTitle: String? = null,
)

data class LabTestDetail(
    var id: Long = -1L,
    var header: String? = null,
    val title1: String,
    val description1: String? = "N/A",
    val title2: String,
    val description2: String? = "N/A",
    val title3: String,
    val description3: String? = "N/A",
    val viewType: Int = LabTestDetailViewModel.ITEM_VIEW_TYPE_RECORD
)