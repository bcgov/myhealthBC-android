package ca.bc.gov.bchealth.ui.dependents.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.repository.DependentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DependentsManagementViewModel @Inject constructor(
    private val repository: DependentsRepository,
) : ViewModel() {

    val dependents = repository.getAllDependents()
    private val _uiState = MutableStateFlow(DependentManagementUiState())
    val uiState: StateFlow<DependentManagementUiState> = _uiState.asStateFlow()

    fun deleteDependent(dependent: DependentDto, uiList: List<DependentDto>) =
        viewModelScope.launch {
            displayLoading(true)
            try {
                repository.deleteDependent(dependent)
                updateDependentOrder(uiList)
                displayLoading(false)
            } catch (e: Exception) {
                displayError(e)
            }
        }

    fun updateDependentOrder(dependents: List<DependentDto>) = viewModelScope.launch {
        repository.updateDependentListOrder(dependents)
    }

    private fun displayLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    private fun displayError(error: Throwable) {
        error.printStackTrace()
        _uiState.update { it.copy(isLoading = false, error = error) }
    }

    data class DependentManagementUiState(
        val isLoading: Boolean = false,
        val error: Throwable? = null,
    )
}
