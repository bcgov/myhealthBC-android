package ca.bc.gov.bchealth.ui.dependents.records

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordItem
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.model.ErrorData
import ca.bc.gov.repository.DependentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DependentRecordsViewModel @Inject constructor(
    private val dependentsRepository: DependentsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DependentRecordsUiState())
    val uiState: StateFlow<DependentRecordsUiState> = _uiState.asStateFlow()

    fun loadRecords(patientId: Long, hdid: String) {
        viewModelScope.launch {
            emitLoading(true)
            getRecords(patientId, hdid)
        }
    }

    private suspend fun getRecords(patientId: Long, hdid: String) {
        try {
            dependentsRepository.requestRecordsIfNeeded(patientId, hdid)

            val testResultWithRecords =
                dependentsRepository.getPatientWithTestResultsAndRecords(patientId)

            val patientWithCovidOrderAndTests =
                dependentsRepository.getPatientWithCovidOrdersAndCovidTests(patientId)

            val patientWithImmunizationRecordAndForecast =
                dependentsRepository.getPatientWithImmunizationRecordAndForecast(patientId)

            val covidTestRecords = testResultWithRecords.testResultWithRecords.map {
                it.toUiModel()
            }

            val covidOrders =
                patientWithCovidOrderAndTests.covidOrderAndTests.map { it.toUiModel() }

            val immunizationRecords =
                patientWithImmunizationRecordAndForecast.immunizationRecords.map {
                    it.toUiModel()
                }

            val result = covidTestRecords + covidOrders + immunizationRecords

            _uiState.update {
                it.copy(records = result, onLoading = false)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun handleError(e: Exception) {
        e.printStackTrace()
        when (e) {
            is NetworkConnectionException -> {
                _uiState.tryEmit(
                    DependentRecordsUiState(
                        records = emptyList(),
                        onLoading = false,
                        isConnected = false
                    )
                )
            }
            is MyHealthException -> emitError()
            else -> emitError()
        }
    }

    private fun emitLoading(loading: Boolean) {
        _uiState.update { it.copy(onLoading = loading) }
    }

    private fun emitError(
        @StringRes title: Int = R.string.error,
        @StringRes message: Int = R.string.error_message
    ) = _uiState.tryEmit(
        DependentRecordsUiState(
            errorData = ErrorData(title, message)
        )
    )

    fun resetUiState() {
        _uiState.tryEmit(
            DependentRecordsUiState(
                onLoading = false,
                errorData = null,
                isConnected = true,
                records = emptyList()
            )
        )
    }
}

data class DependentRecordsUiState(
    val onLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val isConnected: Boolean = true,
    val records: List<HealthRecordItem> = emptyList(),
)
