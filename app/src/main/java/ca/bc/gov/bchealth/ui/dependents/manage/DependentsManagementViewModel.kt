package ca.bc.gov.bchealth.ui.dependents.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.repository.DependentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DependentsManagementViewModel @Inject constructor(
    private val repository: DependentsRepository,
) : ViewModel() {

    val dependents = repository.getAllDependents()

    fun deleteDependent(dependent: DependentDto, uiList: List<DependentDto>) =
        viewModelScope.launch {
            try {
                repository.deleteDependent(dependent)
                updateDependentOrder(uiList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun updateDependentOrder(dependents: List<DependentDto>) = viewModelScope.launch {
        repository.updateDependentListOrder(dependents)
    }
}
