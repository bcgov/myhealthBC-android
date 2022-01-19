package ca.bc.gov.bchealth.ui.healthrecord.individual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.repository.testrecord.TestResultRepository
import ca.bc.gov.repository.vaccine.VaccineRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _uiState = MutableSharedFlow<IndividualHealthRecordsUiState>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val uiState: SharedFlow<IndividualHealthRecordsUiState> = _uiState.asSharedFlow()

    fun getIndividualsHealthRecord(patientId: Long) = viewModelScope.launch {
        val vaccineRecords = vaccineRecordRepository.getVaccineRecords(patientId)
        val tesRecords = testResultRepository.getTestResults(patientId)
        _uiState.tryEmit(
            IndividualHealthRecordsUiState().copy(
                onLoading = false,
                onTestRecords = tesRecords.map { it.toUiModel() },
                onVaccineRecord = vaccineRecords.map { it.toUiModel() }
            )
        )
    }
}

data class IndividualHealthRecordsUiState(
    val onLoading: Boolean = false,
    val onVaccineRecord: List<HealthRecordItem> = emptyList(),
    val onTestRecords: List<HealthRecordItem> = emptyList()
)

data class HealthRecordItem(
    val patientId: Long,
    val testResultId: Long = -1L,
    val icon: Int,
    val title: Int,
    val description: Int,
    val date: String,
)
