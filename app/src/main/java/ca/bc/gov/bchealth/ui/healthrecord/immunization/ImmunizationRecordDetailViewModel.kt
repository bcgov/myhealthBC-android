package ca.bc.gov.bchealth.ui.healthrecord.immunization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.repository.immunization.ImmunizationRecordRepository
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
class ImmunizationRecordDetailViewModel @Inject constructor(
    private val immunizationRecordRepository: ImmunizationRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImmunizationRecordDetailUiState(onLoading = true))
    val uiState: StateFlow<ImmunizationRecordDetailUiState> = _uiState.asStateFlow()

    fun getImmunizationRecordDetails(immunizationRecordId: Long) = viewModelScope.launch {

        try {
            val record =
                immunizationRecordRepository.findByImmunizationRecordId(immunizationRecordId)
            _uiState.update { state ->
                state.copy(onLoading = false, immunizationRecordDetailItem = record.toUiModel())
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(onLoading = false)
            }
        }
    }
}

data class ImmunizationRecordDetailUiState(
    val onLoading: Boolean = false,
    val immunizationRecordDetailItem: ImmunizationRecordDetailItem? = null
)

data class ImmunizationRecordDetailItem(
    val id: Long,
    val dueDate: String? = null,
    val status: String? = null,
    val name: String? = null,
    val doseDetails: List<ImmunizationDoseDetailItem> = emptyList()
)

data class ImmunizationDoseDetailItem(
    val id: Long,
    val date: String? = null,
    val productName: String? = null,
    val providerOrClinicName: String? = null,
    val lotNumber: String? = null
)
