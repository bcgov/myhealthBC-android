package ca.bc.gov.bchealth.ui.healthrecord.individual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.yyyy_MM_dd
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.PatientWithTestResultRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.QueueItTokenRepository
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.testrecord.TestRecordRepository
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
    private val patientWithTestResultRepository: PatientWithTestResultRepository,
    private val patientRepository: PatientRepository,
    private val testRecordRepository: TestRecordRepository,
    private val queueItTokenRepository: QueueItTokenRepository,
    private val fetchTestResultRepository: FetchTestResultRepository
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

    fun requestUpdate(patientId: Long, testResultId: Long) = viewModelScope.launch {
        val patient = patientRepository.getPatient(patientId)
        val testRecord = testRecordRepository.getTestRecords(testResultId)
        updateTestResult(
            patient.phn,
            patient.dateOfBirth.toDate(yyyy_MM_dd),
            testRecord.firstOrNull()?.collectionDateTime?.toDate(yyyy_MM_dd)
        )
    }

    private suspend fun updateTestResult(phn: String?, dob: String, collectionDate: String?) {
        try {
            if (phn != null && collectionDate != null) {
                val testResultId =
                    fetchTestResultRepository.fetchTestRecord(phn, dob, collectionDate)
                _uiState.tryEmit(
                    IndividualHealthRecordsUiState(
                        updatedTestResultId = testResultId
                    )
                )
            }
        } catch (e: Exception) {
            when (e) {
                is MustBeQueuedException -> {
                    _uiState.tryEmit(
                        IndividualHealthRecordsUiState(
                            onLoading = true,
                            queItTokenUpdated = false,
                            onMustBeQueued = true,
                            queItUrl = e.message
                        )
                    )
                }
            }
        }
    }

    fun setQueItToken(token: String?) = viewModelScope.launch {
        queueItTokenRepository.setQueItToken(token)
        _uiState.tryEmit(
            IndividualHealthRecordsUiState(
                onLoading = false, queItTokenUpdated = true
            )
        )
    }
}

data class IndividualHealthRecordsUiState(
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val onVaccineRecord: List<HealthRecordItem> = emptyList(),
    val onTestRecords: List<HealthRecordItem> = emptyList(),
    val updatedTestResultId: Long = -1L
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
