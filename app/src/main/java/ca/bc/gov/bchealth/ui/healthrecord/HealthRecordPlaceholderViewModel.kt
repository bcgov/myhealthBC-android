package ca.bc.gov.bchealth.ui.healthrecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.repository.patient.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthRecordPlaceholderViewModel @Inject constructor(
    private val repository: PatientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientRecordsState())
    val uiState: StateFlow<PatientRecordsState> = _uiState.asStateFlow()

    fun getPatientsAndHealthRecordCounts() = viewModelScope.launch {
        _uiState.update { state ->
            state.copy(isLoading = true)
        }
        val patientHealthRecords = repository.getPatientHealthRecordCount().map { records ->
            records.toUiModel()
        }
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                patientsAndHealthRecordCounts = patientHealthRecords
            )
        }
    }

    fun resetUiState() {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                patientsAndHealthRecordCounts = null
            )
        }
    }
}

data class PatientRecordsState(
    val isLoading: Boolean = false,
    val patientsAndHealthRecordCounts: List<PatientHealthRecord>? = null
)

data class PatientHealthRecord(
    val patientId: Long,
    val name: String,
    val totalRecord: Int,
    val authStatus: AuthenticationStatus
)
