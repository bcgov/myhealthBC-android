package ca.bc.gov.bchealth.ui.healthrecord

import android.util.Log
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.bchealth.ui.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.workers.WorkerInvoker
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.exceptions.ServiceDownException
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.common.model.relation.PatientWithMedicationRecordDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toStartOfDayInstant
import ca.bc.gov.repository.CacheRepository
import ca.bc.gov.repository.MedicationRecordRepository
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
class HealthRecordViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val medicationRecordRepository: MedicationRecordRepository,
    private val cacheRepository: CacheRepository,
    private val workerInvoker: WorkerInvoker,
    private val mobileConfigRepository: MobileConfigRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(HealthRecordUiState())
    val uiState: StateFlow<HealthRecordUiState> = _uiState.asStateFlow()

    fun showTimeLine(filterString: String) = viewModelScope.launch {
        val healthRecords = generateTimeline()
        val requiredProtectiveWordVerification =
            !(healthRecords.any { record -> record.healthRecordType == HealthRecordType.MEDICATION_RECORD } && isShowMedicationRecords())

        val timeLineFilters = mutableListOf<String>()
        val filteredResult = mutableListOf<HealthRecordItem>()
        if (filterString.isNotBlank()) {
            val filterQuery = filterString.split(",")

            val fromDate = filterQuery.find { it.contains("FROM:") }?.substringAfter(":")
            val toDate = filterQuery.find { it.contains("TO:") }?.substringAfter(":")
            val search = filterQuery.find { it.contains("SEARCH:") }?.substringAfter(":")

            val listFilteredByDate = getFilterByDate(healthRecords, fromDate, toDate)

            val listFilteredBySearch = if (!search.isNullOrBlank()) {
                listFilteredByDate.filter { record ->
                    record.title.contains(search, true)
                }
            } else {
                listFilteredByDate
            }

            timeLineFilters +=
                filterQuery.mapNotNull { query -> TimelineTypeFilter.findByName(query)?.recordType?.name }

            filteredResult += if (timeLineFilters.isNotEmpty()) {
                listFilteredBySearch.filter { recordType -> timeLineFilters.contains(recordType.healthRecordType.name) }
            } else {
                listFilteredBySearch
            }

            val dateFilter = getDateFilter(fromDate, toDate)
            dateFilter?.let {
                timeLineFilters.add(it)
            }
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                healthRecords = filteredResult,
                filters = timeLineFilters,
                requiredProtectiveWordVerification = requiredProtectiveWordVerification
            )
        }
    }

    private fun getDateFilter(fromDate: String?, toDate: String?): String? {
        if (fromDate.isNullOrBlank() && toDate.isNullOrBlank()) {
            return null
        }
        return when {
            fromDate.isNullOrBlank() -> {
                "$toDate and before"
            }

            toDate.isNullOrBlank() -> {
                "$fromDate and after"
            }

            else -> {
                "$fromDate - $toDate"
            }
        }
    }

    private fun getFilterByDate(healthRecords: List<HealthRecordItem>, fromDate: String?, toDate: String?): MutableList<HealthRecordItem> {
        return if (!fromDate.isNullOrBlank() && !toDate.isNullOrBlank()) {
            healthRecords.filter { it.date.toStartOfDayInstant() >= fromDate.toDate() && it.date <= toDate.toDate() }
                .toMutableList()
        } else if (!fromDate.isNullOrBlank()) {
            healthRecords.filter { it.date.toStartOfDayInstant() >= fromDate.toDate() }
                .toMutableList()
        } else if (!toDate.isNullOrBlank()) {
            healthRecords.filter { it.date.toStartOfDayInstant() <= toDate.toDate() }.toMutableList()
        } else {
            healthRecords.toMutableList()
        }
    }

    fun showProgressBar() {
        _uiState.update {
            it.copy(isLoading = true)
        }
    }

    private suspend fun generateTimeline(): List<HealthRecordItem> {
        try {
            val patientId =
                patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED).id
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

            val patientWithData = patientRepository.getPatientWithData(patientId)

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

            val diagnosticImaging = patientWithData.toUiModel()

            val records = covidOrders +
                labTestRecords +
                immunizationRecords +
                healthVisits +
                specialAuthorities +
                hospitalVisits +
                clinicalDocuments +
                diagnosticImaging +
                if (isShowMedicationRecords() && medicationRecords != null) {
                    medicationRecords
                } else {
                    emptyList()
                }
            return records
        } catch (e: Exception) {
            Log.d("Timeline", "Error in generating timeline ${e.message}")
            return emptyList()
        }
    }

    private fun isShowMedicationRecords(): Boolean {
        return medicationRecordRepository.getProtectiveWordState() == ProtectiveWordState.PROTECTIVE_WORD_NOT_REQUIRED.value ||
            cacheRepository.isProtectiveWordAdded()
    }

    fun executeOneTimeDataFetch() = viewModelScope.launch {
        try {
            mobileConfigRepository.refreshMobileConfiguration()
            workerInvoker.executeOneTimeDataFetch()
        } catch (e: Exception) {
            when (e) {
                is NetworkConnectionException -> {
                    // TODO: handle error
                }

                is ServiceDownException -> {
                    // TODO: handle error
                }

                else -> {
                    e.printStackTrace()
                }
            }
        }
    }
}

data class HealthRecordUiState(
    val isLoading: Boolean = true,
    val healthRecords: List<HealthRecordItem> = emptyList(),
    val requiredProtectiveWordVerification: Boolean = true,
    val notes: List<String> = emptyList(),
    val filters: List<String> = emptyList()
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

enum class HealthRecordType {
    COVID_TEST_RECORD,
    MEDICATION_RECORD,
    LAB_RESULT_RECORD,
    IMMUNIZATION_RECORD,
    HEALTH_VISIT_RECORD,
    SPECIAL_AUTHORITY_RECORD,
    HOSPITAL_VISITS_RECORD,
    CLINICAL_DOCUMENT_RECORD,
    DIAGNOSTIC_IMAGING
}
