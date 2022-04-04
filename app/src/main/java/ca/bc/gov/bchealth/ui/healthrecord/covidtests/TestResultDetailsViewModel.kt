package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.relation.TestResultWithRecordsAndPatientDto
import ca.bc.gov.common.model.relation.TestResultWithRecordsDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestAndPatientDto
import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.model.test.TestResultDto
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
class TestResultDetailsViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val testResultRepository: TestResultRepository,
    private val covidOrderRepository: CovidOrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestResultDetailUiState())
    val uiState: StateFlow<TestResultDetailUiState> = _uiState.asStateFlow()

    fun getTestResultDetail(testResultId: Long) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true)
        }

        val patientTestResult =
            patientRepository.getPatientWithTestResultAndRecords(testResultId)
        _uiState.update {
            it.copy(onLoading = false, onTestResultDetail = patientTestResult)
        }
    }

    fun getCovidOrderWithCovidTests(covidOrderId: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true)
        }

        val covidOrder = covidOrderRepository.findByCovidOrderId(covidOrderId)
        _uiState.update {
            it.copy(onLoading = false, onTestResultDetail = covidOrder.toTestResult())
        }
    }

    fun deleteTestRecord(testResultId: Long) = viewModelScope.launch {
        testResultRepository.delete(testResultId)
    }
}

data class TestResultDetailUiState(
    val onLoading: Boolean = false,
    val onTestResultDetail: TestResultWithRecordsAndPatientDto? = null
)

fun CovidOrderWithCovidTestAndPatientDto.toTestResult(): TestResultWithRecordsAndPatientDto {

    val testResult = TestResultDto(
        collectionDate = covidOrderWithCovidTest.covidTests[0].collectedDateTime
    )

    val testRecords = covidOrderWithCovidTest.covidTests.map { it.toTestRecord() }

    return TestResultWithRecordsAndPatientDto(
        patient = patient,
        testResultWithRecords = TestResultWithRecordsDto(testResult, testRecords)
    )
}

fun CovidTestDto.toTestRecord(): TestRecordDto {
    return TestRecordDto(
        id = id,
        labName = "",
        collectionDateTime = collectedDateTime,
        resultDateTime = resultDateTime,
        testName = loIncName ?: "",
        testOutcome = labResultOutcome ?: "",
        testType = testType,
        testStatus = testStatus ?: "",
        resultTitle = "",
        resultDescription = resultDescription,
        resultLink = resultLink ?: ""
    )
}
