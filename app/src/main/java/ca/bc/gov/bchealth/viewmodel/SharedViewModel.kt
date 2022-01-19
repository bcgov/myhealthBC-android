package ca.bc.gov.bchealth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _modifiedRecordId: MutableLiveData<Long> = MutableLiveData()
    val modifiedRecordId: LiveData<Long>
        get() = _modifiedRecordId

    fun setModifiedRecordId(patientId: Long) {
        _modifiedRecordId.value = patientId
    }
}