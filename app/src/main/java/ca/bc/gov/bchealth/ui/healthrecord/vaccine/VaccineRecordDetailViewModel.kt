package ca.bc.gov.bchealth.ui.healthrecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.relation.PatientAndVaccineRecord
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.vaccine.VaccineDoseRepository
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
    private val patientWithVaccineRepository: PatientWithVaccineRecordRepository,
    private val vaccineDoseRepository: VaccineDoseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaccineRecordDetailUiState())
    val uiState: StateFlow<VaccineRecordDetailUiState> = _uiState.asStateFlow()

    fun getVaccineRecordDetails(patientId: Long) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true)
        }
        val patientAndVaccine = patientWithVaccineRepository.getPatientWithVaccine(patientId)
        if (patientAndVaccine.vaccineRecord != null) {
            val doses = vaccineDoseRepository.getVaccineDoses(patientAndVaccine.vaccineRecord!!.id)
                .sortedBy { it.date }
            patientAndVaccine.vaccineRecord?.doses = doses
            _uiState.update {
                it.copy(
                    onLoading = false,
                    onVaccineRecordDetail = patientAndVaccine
                )
            }
        }
    }
}

data class VaccineRecordDetailUiState(
    val onLoading: Boolean = false,
    val onVaccineRecordDetail: PatientAndVaccineRecord? = null
)
