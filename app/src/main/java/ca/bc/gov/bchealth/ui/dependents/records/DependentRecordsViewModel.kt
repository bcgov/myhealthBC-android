package ca.bc.gov.bchealth.ui.dependents.records

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.model.ErrorData
import ca.bc.gov.common.model.immunization.ImmunizationDto
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestDto
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.immunization.ImmunizationRecordRepository
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DependentRecordsViewModel @Inject constructor(
    private val bcscAuthRepo: BcscAuthRepo,
    private val immunizationRecordRepository: ImmunizationRecordRepository,
    private val covidOrderRepository: CovidOrderRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DependentRecordsUiState())
    val uiState: StateFlow<DependentRecordsUiState> = _uiState.asStateFlow()

    fun loadRecords(hdid: String) {
        viewModelScope.launch {
            emitLoading(true)

            try {
                val token = bcscAuthRepo.getAuthParametersDto().token
                fetchCovidTestResults(token, hdid)
                fetchImmunizations(token, hdid)

                emitLoading(false)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private suspend fun fetchCovidTestResults(
        token: String,
        hdid: String
    ): List<CovidOrderWithCovidTestDto>? {
        try {
            return covidOrderRepository.fetchCovidOrders(token, hdid)
        } catch (e: Exception) {
            handleError(e)
        }
        return null
    }

    private suspend fun fetchImmunizations(token: String, hdid: String): ImmunizationDto? {
        try {
            return immunizationRecordRepository.fetchImmunization(token, hdid)
        } catch (e: Exception) {
            handleError(e)
        }
        return null
    }

    private fun handleError(e: Exception) {
        e.printStackTrace()
        when (e) {
            is NetworkConnectionException -> {
                _uiState.tryEmit(
                    DependentRecordsUiState(
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
            )
        )
    }
}

data class DependentRecordsUiState(
    val onLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val isConnected: Boolean = true,
)
