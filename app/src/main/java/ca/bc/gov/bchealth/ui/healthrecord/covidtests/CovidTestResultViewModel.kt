package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.CovidOrderDto
import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 25,February,2022
*/
@HiltViewModel
class CovidTestResultViewModel @Inject constructor(
    private val covidOrderRepository: CovidOrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CovidTestResultDetailUiModel())
    val uiState: StateFlow<CovidTestResultDetailUiModel> = _uiState.asStateFlow()

    fun getCovidTestDetail(covidOrderId: String, covidTestId: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true)
        }

        try {
            val covidOrder =
                covidOrderRepository.findByCovidOrderId(covidOrderId)
            val covidTest: CovidTestDto? =
                covidOrder.covidOrderWithCovidTest.covidTests.find { it.id == covidTestId }
            _uiState.update {
                it.copy(
                    onLoading = false,
                    covidOrder = covidOrder.covidOrderWithCovidTest.covidOrder,
                    covidTest = covidTest,
                    patient = covidOrder.patient
                )
            }
        } catch (e: Exception) {
            // no implementation required
        }
    }

    fun getCovidTestInPdf(reportId: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true)
        }
        try {
            val pdfData = covidOrderRepository.fetchCovidTestPdf(reportId, true)
            _uiState.update {
                it.copy(
                    pdfData = pdfData,
                    onLoading = false
                )
            }
        } catch (e: java.lang.Exception) {
            _uiState.update {
                it.copy(
                    onError = true,
                    onLoading = false
                )
            }
        }
    }

    fun resetUiState() {
        _uiState.update {
            it.copy(
                onLoading = false,
                covidOrder = null,
                covidTest = null,
                patient = null,
                pdfData = null,
                onError = false
            )
        }
    }
}

data class CovidTestResultDetailUiModel(
    val onLoading: Boolean = false,
    val covidOrder: CovidOrderDto? = null,
    val covidTest: CovidTestDto? = null,
    val patient: PatientDto? = null,
    val pdfData: String? = null,
    val onError: Boolean = false,
)
