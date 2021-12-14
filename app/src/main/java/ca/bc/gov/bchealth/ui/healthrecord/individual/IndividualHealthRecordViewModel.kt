package ca.bc.gov.bchealth.ui.healthrecord.individual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.VaccineRecord
import ca.bc.gov.common.model.test.TestResult
import ca.bc.gov.repository.testrecord.TestResultRepository
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
class IndividualHealthRecordViewModel @Inject constructor(
    private val vaccineRecordRepository: VaccineRecordRepository,
    private val testResultRepository: TestResultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IndividualHealthRecordsUiState())
    val uiState: StateFlow<IndividualHealthRecordsUiState> = _uiState.asStateFlow()

    fun getIndividualsHealthRecord(patientId: Long) = viewModelScope.launch {
        val vaccineRecords = vaccineRecordRepository.getVaccineRecords(patientId)
        val tesRecords = testResultRepository.getTestResults(patientId)
        _uiState.update {
            it.copy(onLoading = false, onTestRecords = tesRecords, onVaccineRecord = vaccineRecords)
        }
    }
}

data class IndividualHealthRecordsUiState(
    val onLoading: Boolean = false,
    val onVaccineRecord: List<VaccineRecord> = emptyList(),
    val onTestRecords: List<TestResult> = emptyList()
)