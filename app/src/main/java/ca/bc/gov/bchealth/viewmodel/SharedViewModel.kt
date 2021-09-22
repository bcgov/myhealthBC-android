package ca.bc.gov.bchealth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.model.ImmunizationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *[SharedViewModel]
 *
 * @author Amit Metri
 */
@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _status: MutableLiveData<Pair<String, ImmunizationStatus>> = MutableLiveData()
    val status: LiveData<Pair<String, ImmunizationStatus>>
        get() = _status

    fun setStatus(status: Pair<String, ImmunizationStatus>) {
        _status.value = status
    }
}
