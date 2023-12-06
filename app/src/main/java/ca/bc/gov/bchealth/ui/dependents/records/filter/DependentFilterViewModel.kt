package ca.bc.gov.bchealth.ui.dependents.records.filter

import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.filter.FilterViewModel
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DependentFilterViewModel @Inject constructor(
    private val mobileConfigRepository: MobileConfigRepository
) : FilterViewModel() {
    private val _uiState = MutableStateFlow(DependentFilterUiState())
    val uiState: StateFlow<DependentFilterUiState> = _uiState.asStateFlow()

    fun getAvailableFilters() = viewModelScope.launch {
        val filter = mutableListOf<Int>()
        val dataSetFeatureFlag = mobileConfigRepository.getDependentDataSetFeatureFlags()
        filter.add(R.id.chip_date)

        if (dataSetFeatureFlag.isClinicalDocumentEnabled()) {
            filter.add(R.id.chip_clinical_document)
        }

        if (dataSetFeatureFlag.isCovid19TestResultEnabled()) {
            filter.add(R.id.chip_covid_test)
        }

        if (dataSetFeatureFlag.isDiagnosticImagingEnabled()) {
            filter.add(R.id.chip_diagnostic_imaging)
        }

        if (dataSetFeatureFlag.isHealthVisitEnabled()) {
            filter.add(R.id.chip_health_visit)
        }

        if (dataSetFeatureFlag.isHospitalVisitEnabled()) {
            filter.add((R.id.chip_hospital_visits))
        }

        if (dataSetFeatureFlag.isImmunizationEnabled()) {
            filter.add(R.id.chip_immunizations)
        }

        if (dataSetFeatureFlag.isLabResultEnabled()) {
            filter.add(R.id.chip_lab_results)
        }

        if (dataSetFeatureFlag.isMedicationEnabled()) {
            filter.add(R.id.chip_medication)
        }

        if (dataSetFeatureFlag.isSpecialAuthorityRequestEnabled()) {
            filter.add(R.id.chip_special_authority)
        }

        _uiState.update { it.copy(availableFilters = filter) }
    }
}

data class DependentFilterUiState(
    val availableFilters: List<Int> = emptyList()
)
