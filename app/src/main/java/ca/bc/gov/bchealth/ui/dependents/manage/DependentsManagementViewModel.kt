package ca.bc.gov.bchealth.ui.dependents.manage

import androidx.lifecycle.ViewModel
import ca.bc.gov.repository.DependentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DependentsManagementViewModel @Inject constructor(
    private val dependentsRepository: DependentsRepository,
) : ViewModel() {

    val dependents = dependentsRepository.getAllDependents()
}