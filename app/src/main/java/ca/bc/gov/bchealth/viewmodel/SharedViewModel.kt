package ca.bc.gov.bchealth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    var displayImmunizationBanner = true

    private val _modifiedRecordId: MutableLiveData<Long> = MutableLiveData()
    val modifiedRecordId: LiveData<Long>
        get() = _modifiedRecordId

    fun setModifiedRecordId(patientId: Long) {
        _modifiedRecordId.value = patientId
    }

    var destinationId: Int = 0
    var isBCSCAuthShown = false
    var isBiometricAuthShown = false
}
