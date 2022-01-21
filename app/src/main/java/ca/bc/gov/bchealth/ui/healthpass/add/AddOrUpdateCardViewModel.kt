package ca.bc.gov.bchealth.ui.healthpass.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.qr.ProcessQrRepository
import ca.bc.gov.repository.qr.VaccineRecordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
        MutableSharedFlow<AddCardOptionUiState>(replay = 1, extraBufferCapacity = 1)
    val uiState: SharedFlow<AddCardOptionUiState> = _uiState.asSharedFlow()

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
                _uiState.tryEmit(
                    AddCardOptionUiState().copy(
                        vaccineRecord = result.second,
                        state = Status.CAN_UPDATE
                    )
                )
            }
            VaccineRecordState.CAN_INSERT -> {
                _uiState.tryEmit(
                    AddCardOptionUiState().copy(
                        vaccineRecord = result.second,
                        state = Status.CAN_INSERT
                    )
                )
            }
            VaccineRecordState.DUPLICATE -> {
                _uiState.tryEmit(
                    AddCardOptionUiState().copy(
                        vaccineRecord = result.second,
                        state = Status.DUPLICATE
                    )
                )
            }

            VaccineRecordState.INVALID -> {
                _uiState.tryEmit(
                    AddCardOptionUiState().copy(
                        state = Status.ERROR
                    )
                )
            }
        }
    }

    fun insert(vaccineRecord: PatientVaccineRecord) = viewModelScope.launch {
        _uiState.tryEmit(AddCardOptionUiState().copy(onLoading = true, state = Status.NA))
        val result = repository.insertPatientsVaccineRecord(vaccineRecord)
        if (result > 0) {
            _uiState.tryEmit(
                AddCardOptionUiState().copy(
                    onLoading = false,
                    state = Status.INSERTED,
                    modifiedRecordId = result
                )
            )
        } else {
            _uiState.tryEmit(AddCardOptionUiState().copy(onLoading = false, state = Status.ERROR))
        }
    }

    fun update(vaccineRecord: PatientVaccineRecord) = viewModelScope.launch {
        _uiState.tryEmit(AddCardOptionUiState().copy(onLoading = true, state = Status.NA))
        val result = repository.updatePatientVaccineRecord(vaccineRecord)
        if (result > 0) {
            _uiState.tryEmit(
                AddCardOptionUiState().copy(
                    onLoading = false,
                    state = Status.INSERTED,
                    modifiedRecordId = result,
                    vaccineRecord = vaccineRecord
                )
            )
        } else {
            _uiState.tryEmit(AddCardOptionUiState().copy(onLoading = false, state = Status.ERROR))
        }
    }
}

data class AddCardOptionUiState(
    val onLoading: Boolean = false,
    val vaccineRecord: PatientVaccineRecord? = null,
    val modifiedRecordId: Long = -1L,
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
