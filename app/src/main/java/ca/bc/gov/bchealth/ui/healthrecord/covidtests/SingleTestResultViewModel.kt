package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.repository.patient.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 25,February,2022
*/
@HiltViewModel
class SingleTestResultViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestRecordDetailUiState())
    val uiState: StateFlow<TestRecordDetailUiState> = _uiState.asStateFlow()

    fun getTestRecordDetail(testRecordId: String, testResultId: Long) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true)
        }

        try {
            val patientTestResult =
                patientRepository.getPatientWithTestResultAndRecords(testResultId)
            val testRecordDto: TestRecordDto? =
                patientTestResult.testResultWithRecords.testRecords.find { it.id == testRecordId }
            _uiState.update {
                it.copy(
                    onLoading = false,
                    testRecordDto = testRecordDto,
                    patientDto = patientTestResult.patient
                )
            }
        } catch (e: Exception) {
            // no implementation required
        }
    }
}

data class TestRecordDetailUiState(
    val onLoading: Boolean = false,
    val testRecordDto: TestRecordDto? = null,
    val patientDto: PatientDto? = null
)
