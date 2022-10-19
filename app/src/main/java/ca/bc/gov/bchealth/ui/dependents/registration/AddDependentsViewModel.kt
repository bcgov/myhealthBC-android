package ca.bc.gov.bchealth.ui.dependents.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.DependentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDependentsViewModel @Inject constructor(
    private val dependentsRepository: DependentsRepository,
) : ViewModel() {

    fun registerDependent(
        firstName: String,
        lastName: String,
        dob: String,
        phn: String,
    ) {
        viewModelScope.launch {
            dependentsRepository.addDependent(
                firstName, lastName, dob, phn,
            )
        }
    }
}