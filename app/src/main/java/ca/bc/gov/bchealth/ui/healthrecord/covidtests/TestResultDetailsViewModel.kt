package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.relation.PatientTestResultDto
import ca.bc.gov.repository.PatientWithTestResultRepository
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
class TestResultDetailsViewModel @Inject constructor(
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    private val testResultRepository: TestResultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestResultDetailUiState())
    val uiState: StateFlow<TestResultDetailUiState> = _uiState.asStateFlow()

    fun getTestResultDetail(patientInt: Long, testResultId: Long) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true)
        }
        val patientTestResult =
            patientWithTestResultRepository.getPatientWithTestResult(patientInt, testResultId)
        _uiState.update {
            it.copy(onLoading = false, onTestResultDetail = patientTestResult)
        }
    }

    fun deleteTestRecord(testResultId: Long) = viewModelScope.launch {
        testResultRepository.delete(testResultId)
    }
}

data class TestResultDetailUiState(
    val onLoading: Boolean = false,
    val onTestResultDetail: PatientTestResultDto? = null
)
