package ca.bc.gov.bchealth.ui.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.immunization.ForecastStatus
import ca.bc.gov.repository.DependentsRepository
import ca.bc.gov.repository.patient.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val dependentsRepository: DependentsRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(RecommendationUiState())
    val uiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()

    fun showProgress() {
        _uiState.update { it.copy(isLoading = true) }
    }

    fun loadRecommendations() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val patient =
                patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
            val patientData =
                patientRepository.getPatientWithImmunizationRecommendations(patient.id)

            val records = mutableListOf<PatientWithRecommendations>()
            records.add(
                PatientWithRecommendations(
                    patient.id,
                    patient.fullName,
                    patientData.recommendations.map { recommendation -> recommendation.toUiModel() }
                )
            )

            val patientWithDependents = patientRepository.getPatientWithDependents(patient.id)
            patientWithDependents.dependents.forEach {
                dependentsRepository.requestRecordsIfNeeded(it.patientId, it.hdid)
                val dependentData =
                    dependentsRepository.getPatientWithImmunizationRecommendations(it.patientId)

                records.add(
                    PatientWithRecommendations(
                        it.patientId, it.firstname,
                        dependentData.recommendations.map { recommendation -> recommendation.toUiModel() },
                        isDependent = true
                    )
                )
            }

            _uiState.update { it.copy(isLoading = false, patientWithRecommendations = records) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun expandedCard(id: Long) {
        if (_uiState.value.expandedCardIds.contains(id)) {
            val ids = _uiState.value.expandedCardIds.toMutableSet()
            ids.remove(id)
            _uiState.update { it.copy(expandedCardIds = ids) }
        } else {
            val ids = _uiState.value.expandedCardIds.toMutableSet()
            ids.add(id)
            _uiState.update { it.copy(expandedCardIds = ids) }
        }
    }
}

data class RecommendationUiState(
    val isLoading: Boolean = true,
    val patientWithRecommendations: List<PatientWithRecommendations> = emptyList(),
    val expandedCardIds: Set<Long> = emptySet()
)

data class PatientWithRecommendations(
    val patientId: Long = 0,
    val name: String? = null,
    val recommendations: List<RecommendationDetailItem>,
    var expanded: Boolean = false,
    var isDependent: Boolean = false
)

data class RecommendationDetailItem(
    val title: String,
    val status: ForecastStatus?,
    val date: String,
    var fullContent: Boolean = false,
)
