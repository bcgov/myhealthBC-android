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
        MutableStateFlow(AddCardOptionUiState())
    val uiState: StateFlow<AddCardOptionUiState> = _uiState.asStateFlow()

    fun processQRCode(uri: Uri) = viewModelScope.launch {

        val result = processQrRepository.processQRCode(uri)
        processResult(result)
    }

    fun processQRCode(shcUri: String) = viewModelScope.launch {
        val result = processQrRepository.processQRCode(shcUri)
        processResult(result)
    }

    fun processResult(result: Pair<VaccineRecordState, PatientVaccineRecord?>) {

        when (result.first) {
            VaccineRecordState.CAN_UPDATE -> {
                _uiState.update { state ->
                    state.copy(vaccineRecord = result.second, state = Status.CAN_UPDATE)
                }
            }
            VaccineRecordState.CAN_INSERT -> {
                _uiState.update { state ->
                    state.copy(vaccineRecord = result.second, state = Status.CAN_INSERT)
                }
            }
            VaccineRecordState.DUPLICATE -> {
                _uiState.update { state ->
                    state.copy(vaccineRecord = result.second, state = Status.DUPLICATE)
                }
            }

            VaccineRecordState.INVALID -> {
                _uiState.update { state ->
                    state.copy(state = Status.ERROR)
                }
            }
        }
    }

    fun resetStatus(){
        _uiState.update { state ->
            state.copy(
                onLoading = false,vaccineRecord = null, modifiedRecordId = -1L,state = null
            )
        }
    }

    fun insert(vaccineRecord: PatientVaccineRecord) = viewModelScope.launch {
        _uiState.update { state -> state.copy(onLoading = true) }
        val result = repository.insertPatientsVaccineRecord(vaccineRecord)
        if (result > 0) {
            _uiState.update { state ->
                state.copy(
                    onLoading = false,
                    state = Status.INSERTED,
                    modifiedRecordId = result
                )
            }
        } else {
            _uiState.update { state -> state.copy(onLoading = false, state = Status.ERROR) }
        }
    }

    fun update(vaccineRecord: PatientVaccineRecord) = viewModelScope.launch {
        _uiState.update { state -> state.copy(onLoading = true) }
        val result = repository.updatePatientVaccineRecord(vaccineRecord)
        if (result > 0) {
            _uiState.update { state ->
                state.copy(
                    onLoading = false,
                    state = Status.UPDATED,
                    modifiedRecordId = result,
                    vaccineRecord = vaccineRecord
                )
            }
        } else {
            _uiState.update { state -> state.copy(onLoading = false, state = Status.ERROR) }
        }
    }
}

data class AddCardOptionUiState(
    val onLoading: Boolean = false,
    val vaccineRecord: PatientVaccineRecord? = null,
    val modifiedRecordId: Long = -1L,
    val state: Status? = null
)

enum class Status {
    CAN_UPDATE,
    CAN_INSERT,
    DUPLICATE,
    INSERTED,
    UPDATED,
    ERROR
}
