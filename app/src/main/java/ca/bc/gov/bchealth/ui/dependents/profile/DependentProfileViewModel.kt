package ca.bc.gov.bchealth.ui.dependents.profile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.repository.DependentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DependentProfileViewModel @Inject constructor(
    private val repository: DependentsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DependentProfileUiState())
    val uiState: StateFlow<DependentProfileUiState> = _uiState.asStateFlow()

    fun loadInformation(patientId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val dependentDto = repository.getDependent(patientId)
                val data = listOf(
                    DependentProfileItem(
                        label = R.string.dependents_profile_first,
                        value = dependentDto.firstname
                    ),
                    DependentProfileItem(
                        label = R.string.dependents_profile_last,
                        value = dependentDto.lastname
                    ),
                    DependentProfileItem(
                        label = R.string.dependents_profile_phn,
                        value = dependentDto.phn
                    ),
                    DependentProfileItem(
                        label = R.string.dependents_profile_dob,
                        value = dependentDto.dateOfBirth.toDate()
                    ),
                    DependentProfileItem(
                        label = R.string.access_count,
                        value = dependentDto.dateOfBirth.toDate()
                    ),
                )

                _uiState.update {
                    it.copy(
                        dependentInfo = data,
                        dto = dependentDto,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e, isLoading = false) }
            }
        }
    }

    fun removeDependent(patientId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.deleteDependent(patientId)
                _uiState.update { it.copy(isLoading = false, onDependentRemoved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e, isLoading = false) }
            }
        }
    }

    fun resetErrorState() {
        _uiState.update { it.copy(error = null) }
    }

    data class DependentProfileUiState(
        val dependentInfo: List<DependentProfileItem> = emptyList(),
        val dto: DependentDto? = null,
        val isLoading: Boolean = false,
        val error: Exception? = null,
        val onDependentRemoved: Boolean = false,
        val totalDelegateCount: Long = 0
    )

    data class DependentProfileItem(
        @StringRes val label: Int,
        val value: String,
    )
}
