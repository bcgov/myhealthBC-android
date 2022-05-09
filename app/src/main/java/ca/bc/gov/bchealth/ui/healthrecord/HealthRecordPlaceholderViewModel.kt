package ca.bc.gov.bchealth.ui.healthrecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.patient.PatientDto
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

    fun getBcscAuthPatient() = viewModelScope.launch {
        try {
            val bcscAuthPatient = repository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
            _uiState.update { state ->
                state.copy(
                    isBcscAuthenticatedPatientAvailable = BcscAuthPatientAvailability.AVAILABLE,
                    bcscAuthenticatedPatientDto = bcscAuthPatient
                )
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(
                    isBcscAuthenticatedPatientAvailable = BcscAuthPatientAvailability.NOT_AVAILABLE,
                    bcscAuthenticatedPatientDto = null
                )
            }
        }
    }

    fun resetUiState() {
        _uiState.update { state ->
            state.copy(
                isBcscAuthenticatedPatientAvailable = null,
                bcscAuthenticatedPatientDto = null
            )
        }
    }
}

data class PatientRecordsState(
    val isBcscAuthenticatedPatientAvailable: BcscAuthPatientAvailability? = null,
    val bcscAuthenticatedPatientDto: PatientDto? = null
)

data class PatientHealthRecord(
    val patientId: Long,
    val name: String,
    val totalRecord: Int,
    val authStatus: AuthenticationStatus
)

enum class BcscAuthPatientAvailability {
    AVAILABLE,
    NOT_AVAILABLE
}
