package ca.bc.gov.bchealth.ui.healthrecord.immunization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImmunizationRecordDetailViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ImmunizationRecordDetailUiState())
    val uiState: StateFlow<ImmunizationRecordDetailUiState> = _uiState.asStateFlow()

    fun getImmunizationRecordDetails(patientId: Long) = viewModelScope.launch {
    }
}

data class ImmunizationRecordDetailUiState(
    val onLoading: Boolean = false,
)
