package ca.bc.gov.bchealth.ui.healthrecord.individual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.bchealth.workers.WorkerInvoker
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.exceptions.ServiceDownException
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.relation.PatientWithMedicationRecordDto
import ca.bc.gov.repository.CacheRepository
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
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
    private val cacheRepository: CacheRepository,
    private val workerInvoker: WorkerInvoker,
    private val mobileConfigRepository: MobileConfigRepository
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

    private fun getIndividualsHealthRecord(patientId: Long) =
        viewModelScope.launch {

            try {
                var patientAndMedicationRecords: PatientWithMedicationRecordDto? = null
                try {
                    patientAndMedicationRecords =
                        patientRepository.getPatientWithMedicationRecords(patientId)
                } catch (e: Exception) {
                    medicationRecordRepository.deleteMedicationData(patientId)
                }
                val patientWithLabOrdersAndLabTests =
                    patientRepository.getPatientWithLabOrdersAndLabTests(patientId)
                val patientWithCovidOrderAndTests =
                    patientRepository.getPatientWithCovidOrdersAndCovidTests(patientId)
                val patientWithImmunizationRecordAndForecast =
                    patientRepository.getPatientWithImmunizationRecordAndForecast(patientId)
                val patientWithHealthVisits =
                    patientRepository.getPatientWithHealthVisits(patientId)
                val patientWithSpecialAuthorities =
                    patientRepository.getPatientWithSpecialAuthority(patientId)
                val hospitalVisits = patientRepository.getPatientWithHospitalVisits(patientId).map {
                    it.toUiModel()
                }
                val clinicalDocuments = patientRepository.getPatientWithClinicalDocuments(patientId)
                    .map { it.toUiModel() }

                val medicationRecords = patientAndMedicationRecords?.medicationRecord?.map {
                    it.toUiModel()
                }
                val labTestRecords = patientWithLabOrdersAndLabTests.labOrdersWithLabTests.map {
                    it.toUiModel()
                }
                val covidOrders =
                    patientWithCovidOrderAndTests.covidOrderAndTests.map { it.toUiModel() }

                val immunizationRecords =
                    patientWithImmunizationRecordAndForecast.immunizationRecords.map { it.toUiModel() }

                val healthVisits = patientWithHealthVisits.healthVisits.map {
                    it.toUiModel()
                }
                val specialAuthorities = patientWithSpecialAuthorities.specialAuthorities.filter {
                    it.requestedDate != null
                }.map { it.toUiModel() }

                val bcscInfo = getBcscInfo()

                val records = covidOrders +
                    labTestRecords +
                    immunizationRecords +
                    healthVisits +
                    specialAuthorities +
                    hospitalVisits +
                    clinicalDocuments +
                    if (isShowMedicationRecords() && medicationRecords != null) {
                        medicationRecords
                    } else {
                        emptyList()
                    }

                _uiState.update { state ->
                    state.copy(
                        isBcscAuthenticatedPatientAvailable = bcscInfo.authenticationAvailable,
                        isBcscSessionActive = bcscInfo.sessionActive,
                        bcscAuthenticatedPatientDto = bcscInfo.patientDto,
                        onHealthRecords = records.sortedByDescending { it.date },
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    fun isShowMedicationRecords(): Boolean {
        return medicationRecordRepository.getProtectiveWordState() == ProtectiveWordState.PROTECTIVE_WORD_NOT_REQUIRED.value ||
            cacheRepository.isProtectiveWordAdded()
    }

    private suspend fun getBcscInfo(): BcscInfo {
        var dto: PatientDto? = null

        val isAuthenticatedPatientAvailable: Boolean = try {
            dto = patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }

        val isBcscSessionActive: Boolean = bcscAuthRepo.checkSession()

        return BcscInfo(dto, isAuthenticatedPatientAvailable, isBcscSessionActive)
    }

    fun executeOneTimeDataFetch() = viewModelScope.launch {
        try {
            mobileConfigRepository.refreshMobileConfiguration()
            workerInvoker.executeOneTimeDataFetch()
        } catch (e: Exception) {
            when (e) {
                is NetworkConnectionException -> {
                    _uiState.update {
                        it.copy(
                            isConnected = false
                        )
                    }
                }
                is ServiceDownException -> {
                    _uiState.update { state ->
                        state.copy(isHgServicesUp = false)
                    }
                }
                else -> {
                    e.printStackTrace()
                }
            }
        }
    }

    fun resetErrorState() {
        _uiState.update { state ->
            state.copy(isHgServicesUp = true, isConnected = true)
        }
    }
}

private class BcscInfo(
    val patientDto: PatientDto?,
    val authenticationAvailable: Boolean,
    val sessionActive: Boolean
)

data class IndividualHealthRecordsUiState(
    val isBcscAuthenticatedPatientAvailable: Boolean? = null,
    val isBcscSessionActive: Boolean? = null,
    val bcscAuthenticatedPatientDto: PatientDto? = null,
    val onHealthRecords: List<HealthRecordItem> = emptyList(),
    val medicationRecordsUpdated: Boolean = false,
    val isHgServicesUp: Boolean = true,
    val isConnected: Boolean = true,
)

data class HealthRecordItem(
    val recordId: Long,
    val patientId: Long,
    val icon: Int,
    val title: String,
    val description: String,
    val date: Instant,
    val dataSource: String?,
    val healthRecordType: HealthRecordType,
)

data class HiddenRecordItem(
    val countOfRecords: Int
)

enum class HealthRecordType {
    COVID_TEST_RECORD,
    MEDICATION_RECORD,
    LAB_RESULT_RECORD,
    IMMUNIZATION_RECORD,
    HEALTH_VISIT_RECORD,
    SPECIAL_AUTHORITY_RECORD,
    HOSPITAL_VISITS_RECORD,
    CLINICAL_DOCUMENT_RECORD
}

data class HiddenMedicationRecordItem(
    val patientId: Long,
    val title: String,
    val desc: String
)
