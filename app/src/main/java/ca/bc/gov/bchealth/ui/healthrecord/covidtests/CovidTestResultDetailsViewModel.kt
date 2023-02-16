package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.exceptions.ServiceDownException
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestAndPatientDto
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
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
class CovidTestResultDetailsViewModel @Inject constructor(
    private val covidOrderRepository: CovidOrderRepository,
    private val mobileConfigRepository: MobileConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CovidResultDetailUiState())
    val uiState: StateFlow<CovidResultDetailUiState> = _uiState.asStateFlow()

    fun getCovidOrderWithCovidTests(orderId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(onLoading = true) }

        val covidOrder = covidOrderRepository.findByCovidOrderId(orderId)
        _uiState.update {
            it.copy(onLoading = false, onCovidTestResultDetail = covidOrder)
        }
    }

    fun getCovidTestInPdf(orderId: Long, reportId: String) = viewModelScope.launch {
        _uiState.update { it.copy(onLoading = true) }

        try {
            mobileConfigRepository.refreshMobileConfiguration()

            val pdfData = covidOrderRepository.fetchCovidTestPdf(orderId, reportId, true)
            _uiState.update {
                it.copy(
                    pdfData = pdfData,
                    onLoading = false
                )
            }
        } catch (e: java.lang.Exception) {
            when (e) {
                is NetworkConnectionException -> {
                    _uiState.update {
                        it.copy(
                            onLoading = false,
                            isConnected = false
                        )
                    }
                }
                is ServiceDownException -> _uiState.update {
                    it.copy(isHgServicesUp = false, onLoading = false)
                }
                else -> {
                    _uiState.update {
                        it.copy(
                            onLoading = false,
                            onError = true
                        )
                    }
                }
            }
        }
    }

    fun resetUiState() {
        _uiState.update {
            it.copy(
                onLoading = false,
                onCovidTestResultDetail = null,
                pdfData = null,
                onError = false,
                isHgServicesUp = true,
                isConnected = true
            )
        }
    }
}

data class CovidResultDetailUiState(
    val onLoading: Boolean = false,
    val onCovidTestResultDetail: CovidOrderWithCovidTestAndPatientDto? = null,
    val pdfData: String? = null,
    val onError: Boolean = false,
    val isHgServicesUp: Boolean = true,
    val isConnected: Boolean = true
)
