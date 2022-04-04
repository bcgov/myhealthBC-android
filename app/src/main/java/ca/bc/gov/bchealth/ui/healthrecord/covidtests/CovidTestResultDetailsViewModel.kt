package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestAndPatientDto
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import ca.bc.gov.repository.testrecord.TestResultRepository
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
class CovidTestResultDetailsViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val testResultRepository: TestResultRepository,
    private val covidOrderRepository: CovidOrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CovidResultDetailUiState())
    val uiState: StateFlow<CovidResultDetailUiState> = _uiState.asStateFlow()

    fun getCovidOrderWithCovidTests(covidOrderId: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true)
        }

        val covidOrder = covidOrderRepository.findByCovidOrderId(covidOrderId)
        _uiState.update {
            it.copy(onLoading = false, onCovidTestResultDetail = covidOrder)
        }
    }
}

data class CovidResultDetailUiState(
    val onLoading: Boolean = false,
    val onCovidTestResultDetail: CovidOrderWithCovidTestAndPatientDto? = null
)
