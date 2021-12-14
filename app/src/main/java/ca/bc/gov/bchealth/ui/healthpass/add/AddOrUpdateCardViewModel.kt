package ca.bc.gov.bchealth.ui.healthpass.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.PatientWithVaccineRecordRepository

import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.qr.ProcessQrRepository
import ca.bc.gov.repository.qr.VaccineRecordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class AddOrUpdateCardViewModel @Inject constructor(
    private val processQrRepository: ProcessQrRepository,
    private val repository: PatientWithVaccineRecordRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(AddCardOptionUiState(onLoading = true, state = Status.NA))
    val uiState: StateFlow<AddCardOptionUiState> = _uiState.asStateFlow()

    fun processQRCode(uri: Uri) = viewModelScope.launch {

        val result = processQrRepository.processQRCode(uri)
        processResult(result)
    }

    fun processQRCode(shcUri: String) = viewModelScope.launch {
        val result = processQrRepository.processQRCode(shcUri)
        processResult(result)
    }

    private fun processResult(result: Pair<VaccineRecordState, PatientVaccineRecord?>) {

        when (result.first) {
            VaccineRecordState.CAN_UPDATE -> {
                _uiState.update {
                    it.copy(
                        vaccineRecord = result.second,
                        state = Status.CAN_UPDATE
                    )
                }
            }
            VaccineRecordState.CAN_INSERT -> {
                _uiState.update {
                    it.copy(
                        vaccineRecord = result.second,
                        state = Status.CAN_INSERT
                    )
                }
            }
            VaccineRecordState.DUPLICATE -> {
                _uiState.update {
                    it.copy(
                        state = Status.DUPLICATE
                    )
                }
            }

            VaccineRecordState.INVALID -> {
                _uiState.update {
                    it.copy(
                        state = Status.ERROR
                    )
                }
            }
        }
        _uiState.update {
            it.copy(onLoading = false, state = Status.NA)
        }
    }

    fun insert(vaccineRecord: PatientVaccineRecord) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true, state = Status.NA)
        }
        val result = repository.insertPatientsVaccineRecord(vaccineRecord)
        _uiState.update {
            val status = if (result > 0) Status.INSERTED else Status.ERROR
            it.copy(state = status)
        }
        _uiState.update {
            it.copy(onLoading = false, state = Status.NA)
        }
    }

    fun update(vaccineRecord: PatientVaccineRecord) = viewModelScope.launch {
        _uiState.update {
            it.copy(onLoading = true, state = Status.NA)
        }
        val result = repository.updatePatientVaccineRecord(vaccineRecord)
        _uiState.update {
            val status = if (result > 0) Status.UPDATED else Status.ERROR
            it.copy(state = status)
        }
        _uiState.update {
            it.copy(onLoading = false, state = Status.NA)
        }
    }
}

data class AddCardOptionUiState(
    val onLoading: Boolean = false,
    val vaccineRecord: PatientVaccineRecord? = null,
    val state: Status = Status.NA
)

enum class Status {
    CAN_UPDATE,
    CAN_INSERT,
    DUPLICATE,
    INSERTED,
    UPDATED,
    ERROR,
    NA
}