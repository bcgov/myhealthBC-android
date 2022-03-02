package ca.bc.gov.bchealth.ui.healthrecord.individual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.yyyy_MM_dd
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.QueueItTokenRepository
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.testrecord.TestResultRepository
import ca.bc.gov.repository.vaccine.VaccineRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class IndividualHealthRecordViewModel @Inject constructor(
    private val vaccineRecordRepository: VaccineRecordRepository,
    private val testResultRepository: TestResultRepository,
    private val patientRepository: PatientRepository,
    private val queueItTokenRepository: QueueItTokenRepository,
    private val fetchTestResultRepository: FetchTestResultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IndividualHealthRecordsUiState())
    val uiState: StateFlow<IndividualHealthRecordsUiState> = _uiState.asStateFlow()

    fun getIndividualsHealthRecord(patientId: Long) = viewModelScope.launch {
        try {
            val patientWithVaccineRecords =
                patientRepository.getPatientWithVaccineAndDoses(patientId)
            val testResultWithRecords =
                patientRepository.getPatientWithTestResultsAndRecords(patientId)
            val vaccineWithDoses = listOfNotNull(patientWithVaccineRecords.vaccineWithDoses)
            val patientAndMedicationRecords =
                patientRepository.getPatientWithMedicationRecords(patientId)

            val covidTestRecords = testResultWithRecords.testResultWithRecords.map {
                it.toUiModel()
            }
            val vaccineRecords = vaccineWithDoses.map {
                it.toUiModel()
            }
            val medicationRecords = patientAndMedicationRecords.medicationRecord.map {
                it.toUiModel()
            }

            val covidTestRecordsNonBcsc = covidTestRecords
                .filter { it.dataSource != DataSource.BCSC.name }
            val vaccineRecordsNonBcsc = vaccineRecords
                .filter { it.dataSource != DataSource.BCSC.name }
            val medicationRecordsNonBcsc = medicationRecords
                .filter { it.dataSource != DataSource.BCSC.name }

            _uiState.update { state ->
                state.copy(
                    onLoading = false,
                    patientAuthStatus = patientWithVaccineRecords.patient.authenticationStatus,
                    authenticatedRecordsCount = patientRepository.getBcscDataRecordCount(),
                    onHealthRecords = (covidTestRecords + vaccineRecords + medicationRecords)
                        .sortedByDescending { it.date },
                    onNonBcscHealthRecords = (
                        covidTestRecordsNonBcsc +
                            vaccineRecordsNonBcsc +
                            medicationRecordsNonBcsc
                        )
                        .sortedByDescending { it.date }
                )
            }
        } catch (e: java.lang.Exception) {
            // no implementation required.
        }
    }

    fun deleteVaccineRecord(patientId: Long) = viewModelScope.launch {
        val patientAndVaccineRecord = patientRepository.getPatientWithVaccineAndDoses(patientId)
        patientAndVaccineRecord.vaccineWithDoses?.vaccine?.id?.let {
            vaccineRecordRepository.delete(vaccineRecordId = it)
        }
    }

    fun deleteTestRecord(testResultId: Long) = viewModelScope.launch {
        testResultRepository.delete(testResultId)
    }

    fun requestUpdate(testResultId: Long) = viewModelScope.launch {
        val testResultWithRecordsAndPatient =
            patientRepository.getPatientWithTestResultAndRecords(testResultId)
        updateTestResult(
            testResultWithRecordsAndPatient.patient.phn,
            testResultWithRecordsAndPatient.patient.dateOfBirth.toDate(yyyy_MM_dd),
            testResultWithRecordsAndPatient.testResultWithRecords.testRecords.firstOrNull()?.collectionDateTime?.toDate(
                yyyy_MM_dd
            )
        )
    }

    private suspend fun updateTestResult(phn: String?, dob: String, collectionDate: String?) {
        try {
            if (phn != null && collectionDate != null) {
                val testResultId =
                    fetchTestResultRepository.fetchTestRecord(phn, dob, collectionDate)
                _uiState.update { state ->
                    state.copy(updatedTestResultId = testResultId)
                }
            }
        } catch (e: Exception) {
            when (e) {
                is MustBeQueuedException -> {
                    _uiState.update { state ->
                        state.copy(
                            onLoading = true,
                            queItTokenUpdated = false,
                            onMustBeQueued = true,
                            queItUrl = e.message
                        )
                    }
                }
            }
        }
    }

    fun setQueItToken(token: String?) = viewModelScope.launch {
        queueItTokenRepository.setQueItToken(token)
        _uiState.update { state ->
            state.copy(
                onLoading = false,
                queItTokenUpdated = true
            )
        }
    }
}

data class IndividualHealthRecordsUiState(
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val updatedTestResultId: Long = -1L,
    val authenticatedRecordsCount: Int? = null,
    val patientAuthStatus: AuthenticationStatus? = null,
    val onHealthRecords: List<HealthRecordItem> = emptyList(),
    val onNonBcscHealthRecords: List<HealthRecordItem> = emptyList(),
)

data class HealthRecordItem(
    val patientId: Long,
    val testResultId: Long = -1L,
    val medicationRecordId: Long = -1L,
    val icon: Int,
    val title: String,
    val description: String,
    val testOutcome: String? = null,
    val date: Instant,
    val healthRecordType: HealthRecordType,
    val dataSource: String? = null
)

data class HiddenRecordItem(
    val countOfRecords: Int
)

enum class HealthRecordType {
    VACCINE_RECORD,
    COVID_TEST_RECORD,
    MEDICATION_RECORD
}
