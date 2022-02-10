package ca.bc.gov.bchealth.ui.healthrecord.individual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.patient.PatientListDto
import ca.bc.gov.repository.patient.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 10,February,2022
*/
@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    fun getPatients() = viewModelScope.launch {
        _uiState.update {
            it.copy(showLoading = true)
        }
        patientRepository.getPatientList().collect() { onPatientListDto ->
            _uiState.update {
                it.copy(onPatientListDto = onPatientListDto)
            }
        }
    }

    fun getTimeLine(patientId: Long) = viewModelScope.launch {
        // TODO: 10/02/22 fetch the time line of health records and update ui state
    }
}

data class TimelineUiState(
    val showLoading: Boolean = false,
    val onPatientListDto: PatientListDto? = null
)
