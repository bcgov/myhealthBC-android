package ca.bc.gov.bchealth.ui.healthrecord.individual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.repository.CacheRepository
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.patient.PatientRepository
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
    private val patientRepository: PatientRepository,
    private val medicationRecordRepository: MedicationRecordRepository,
    private val bcscAuthRepo: BcscAuthRepo,
    private val cacheRepository: CacheRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IndividualHealthRecordsUiState())
    val uiState: StateFlow<IndividualHealthRecordsUiState> = _uiState.asStateFlow()

    fun getIndividualsHealthRecord() = viewModelScope.launch {
        // Patient Id will change post sync of health records
        try {
            val bcscUserPatientId =
                patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED).id
            getIndividualsHealthRecord(
                bcscUserPatientId,
            )
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(
                    isBcscAuthenticatedPatientAvailable = false,
                    isBcscSessionActive = false
                )
            }
        }
    }

    private fun getIndividualsHealthRecord(
        patientId: Long
    ) =
        viewModelScope.launch {

            try {
                val patientWithVaccineRecords =
                    patientRepository.getPatientWithVaccineAndDoses(patientId)
                val testResultWithRecords =
                    patientRepository.getPatientWithTestResultsAndRecords(patientId)
                val vaccineWithDoses = listOfNotNull(patientWithVaccineRecords.vaccineWithDoses)
                val patientAndMedicationRecords =
                    patientRepository.getPatientWithMedicationRecords(patientId)
                val patientWithLabOrdersAndLabTests =
                    patientRepository.getPatientWithLabOrdersAndLabTests(patientId)
                val patientWithCovidOrderAndTests =
                    patientRepository.getPatientWithCovidOrdersAndCovidTests(patientId)

                val covidTestRecords = testResultWithRecords.testResultWithRecords.map {
                    it.toUiModel()
                }
                val vaccineRecords = vaccineWithDoses.map {
                    it.toUiModel()
                }
                val medicationRecords = patientAndMedicationRecords.medicationRecord.map {
                    it.toUiModel()
                }
                val labTestRecords = patientWithLabOrdersAndLabTests.labOrdersWithLabTests.map {
                    it.toUiModel()
                }
                val covidOrders =
                    patientWithCovidOrderAndTests.covidOrderAndTests.map { it.toUiModel() }

                var bcscAuthenticatedPatientDto: PatientDto? = null
                val isBcscAuthenticatedPatientAvailable: Boolean? = try {
                    bcscAuthenticatedPatientDto =
                        patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
                    true
                } catch (e: java.lang.Exception) {
                    false
                }

                val isBcscSessionActive: Boolean = bcscAuthRepo.checkSession()

                val filteredHealthRecords = if (isShowMedicationRecords()) {
                    medicationRecords + covidTestRecords + covidOrders + labTestRecords + vaccineRecords
                } else {
                    covidTestRecords + covidOrders + labTestRecords + vaccineRecords
                }

                _uiState.update { state ->
                    state.copy(
                        isBcscAuthenticatedPatientAvailable = isBcscAuthenticatedPatientAvailable,
                        isBcscSessionActive = isBcscSessionActive,
                        bcscAuthenticatedPatientDto = bcscAuthenticatedPatientDto,
                        onHealthRecords = filteredHealthRecords.sortedByDescending { it.date },
                    )
                }
            } catch (e: java.lang.Exception) {
                // no implementation required.
            }
        }

    fun isShowMedicationRecords(): Boolean {
        return medicationRecordRepository.getProtectiveWordState() == ProtectiveWordState.PROTECTIVE_WORD_NOT_REQUIRED.value ||
            cacheRepository.isProtectiveWordAdded()
    }
}

data class IndividualHealthRecordsUiState(
    val isBcscAuthenticatedPatientAvailable: Boolean? = null,
    val isBcscSessionActive: Boolean? = null,
    val bcscAuthenticatedPatientDto: PatientDto? = null,
    val onHealthRecords: List<HealthRecordItem> = emptyList(),
    val medicationRecordsUpdated: Boolean = false
)

data class HealthRecordItem(
    val patientId: Long,
    val testResultId: Long = -1L,
    val medicationRecordId: Long = -1L,
    val labOrderId: Long = -1L,
    val covidOrderId: String? = null,
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
    MEDICATION_RECORD,
    LAB_TEST
}

data class HiddenMedicationRecordItem(
    val patientId: Long,
    val title: String,
    val desc: String
)
