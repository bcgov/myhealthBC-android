package ca.bc.gov.bchealth.ui.healthrecord.individual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.repository.PatientWithTestResultRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
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
    private val testResultRepository: TestResultRepository,
    private val patientWithVaccineRepository: PatientWithVaccineRecordRepository,
    private val patientWithTestResultRepository: PatientWithTestResultRepository
) : ViewModel() {

    private val _uiState = MutableSharedFlow<IndividualHealthRecordsUiState>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val uiState: SharedFlow<IndividualHealthRecordsUiState> = _uiState.asSharedFlow()

    fun getIndividualsHealthRecord(patientId: Long) = viewModelScope.launch {
        val patientWithVaccineRecords =
            patientWithVaccineRepository.getPatientWithVaccineRecord(patientId)
        val testResultWithRecords =
            patientWithTestResultRepository.getPatientWithTestRecords(patientId)

        val vaccineRecords = listOfNotNull(patientWithVaccineRecords.vaccineRecordDto)
        _uiState.tryEmit(
            IndividualHealthRecordsUiState().copy(
                onLoading = false,
                onTestRecords = testResultWithRecords.testResultWithRecordsDto.map { it.toUiModel() },
                onVaccineRecord = vaccineRecords.map { it.toUiModel() }
            )
        )
    }

    fun deleteVaccineRecord(patientId: Long) = viewModelScope.launch {
        val patientAndVaccineRecord = patientWithVaccineRepository.getPatientWithVaccine(patientId)
        patientAndVaccineRecord.vaccineRecordDto?.id?.let {
            vaccineRecordRepository.delete(vaccineRecordId = it)
        }
    }

    fun deleteTestRecord(testResultId: Long) = viewModelScope.launch {
        testResultRepository.delete(testResultId)
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
    val testOutcome: String? = null,
    val date: String,
    val healthRecordType: HealthRecordType
)

enum class HealthRecordType {
    VACCINE_RECORD,
    COVID_TEST_RECORD
}
