package ca.bc.gov.bchealth.ui.healthrecord.vaccine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.vaccine.VaccineRecordRepository
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
class VaccineRecordDetailViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val vaccineRecordRepository: VaccineRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaccineRecordDetailUiState())
    val uiState: StateFlow<VaccineRecordDetailUiState> = _uiState.asStateFlow()

    fun getVaccineRecordDetails(patientId: Long) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true)
        }
        val patientAndVaccine = patientRepository.getPatientWithVaccineAndDoses(patientId)
        if (patientAndVaccine.vaccineWithDoses != null) {
            _uiState.update {
                it.copy(
                    onLoading = false,
                    onVaccineRecordDtoDetail = patientAndVaccine
                )
            }
        }
    }

    fun deleteVaccineRecord(vaccineRecordId: Long) = viewModelScope.launch {
        vaccineRecordRepository.delete(vaccineRecordId = vaccineRecordId)
    }
}

data class VaccineRecordDetailUiState(
    val onLoading: Boolean = false,
    val onVaccineRecordDtoDetail: PatientWithVaccineAndDosesDto? = null
)
