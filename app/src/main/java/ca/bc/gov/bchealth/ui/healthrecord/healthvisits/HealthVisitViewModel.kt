package ca.bc.gov.bchealth.ui.healthrecord.healthvisits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.repository.healthvisits.HealthVisitsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 22,June,2022
*/
@HiltViewModel
class HealthVisitViewModel @Inject constructor(
    private val healthVisitsRepository: HealthVisitsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthVisitDetailUiState())
    val uiState: StateFlow<HealthVisitDetailUiState> = _uiState.asStateFlow()

    fun fetchHealthVisitDetails(id: Long) = viewModelScope.launch {
        try {
            _uiState.update {
                it.copy(onLoading = true)
            }
            val healthVisitDto = healthVisitsRepository.getHealthVisitDetails(id)

            val uiList = listOf(
                HealthVisitDetailItem(
                    title = R.string.clinic_name,
                    description = healthVisitDto?.clinicDto?.name,

                ),
                HealthVisitDetailItem(
                    title = R.string.practitioner_name,
                    description = healthVisitDto?.practitionerName
                )
            )
            _uiState.update {
                it.copy(
                    onLoading = false,
                    onError = false,
                    title = healthVisitDto?.specialtyDescription,
                    uiList = uiList
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    onError = true,
                    onLoading = false
                )
            }
        }
    }

    fun resetUiState() {
        _uiState.update { HealthVisitDetailUiState() }
    }
}

data class HealthVisitDetailUiState(
    val onLoading: Boolean = false,
    val onError: Boolean = false,
    val title: String? = null,
    val uiList: List<HealthVisitDetailItem> = listOf(),
)

data class HealthVisitDetailItem(
    val title: Int,
    val description: String?,
)
