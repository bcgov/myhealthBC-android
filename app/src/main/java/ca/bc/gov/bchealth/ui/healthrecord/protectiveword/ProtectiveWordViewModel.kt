package ca.bc.gov.bchealth.ui.healthrecord.protectiveword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.common.model.ErrorData
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProtectiveWordViewModel @Inject constructor
    (
    private val medicationRecordRepository: MedicationRecordRepository,
    private val bcscAuthRepo: BcscAuthRepo
) : ViewModel() {

    private val _uiState = MutableSharedFlow<FetchMedicationUiState>(extraBufferCapacity = 1)
    val uiState: SharedFlow<FetchMedicationUiState> = _uiState.asSharedFlow()

    private fun saveProtectiveWord(word: String) {
        medicationRecordRepository.saveProtectiveWord(word)
    }

    private fun isProtectiveWordValid(word: String): Boolean {
        return if (medicationRecordRepository.getProtectiveWord() == null) {
            true
        } else {
            word == medicationRecordRepository.getProtectiveWord()
        }
    }

    fun fetchMedicationRecords(patientId: Long, protectiveWord: String) {
        viewModelScope.launch {
            val isRecordsAvailable = medicationRecordRepository.isMedicationRecordsAvailableForPatient(patientId)
            if(isRecordsAvailable) {
                if(isProtectiveWordValid(protectiveWord)) {
                    _uiState.tryEmit(
                        FetchMedicationUiState(
                            isRecordsUpdated = true
                        )
                    )
                } else {
                    _uiState.tryEmit(
                        FetchMedicationUiState(
                            wrongProtectiveWord = true
                        )
                    )
                }
            } else {
                _uiState.tryEmit(
                    FetchMedicationUiState(
                        onLoading = true
                    )
                )
                try {
                    val authParameters = bcscAuthRepo.getAuthParameters()
                    medicationRecordRepository.fetchMedicationStatement(
                        patientId,
                        authParameters.first,
                        authParameters.second,
                        protectiveWord
                    )
                    saveProtectiveWord(protectiveWord)
                    _uiState.tryEmit(
                        FetchMedicationUiState(
                            onLoading = false,
                            isRecordsUpdated = true
                        )
                    )
                } catch (e: Exception) {
                    when (e) {
                        is ProtectiveWordException -> {
                            _uiState.tryEmit(
                                FetchMedicationUiState(
                                    onLoading = false,
                                    wrongProtectiveWord = true
                                )
                            )
                        }
                        else -> {
                            _uiState.tryEmit(
                                FetchMedicationUiState(
                                    onLoading = false,
                                    errorData = ErrorData(
                                        R.string.error,
                                        R.string.error_message
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

data class FetchMedicationUiState(
    val onLoading: Boolean = false,
    val isRecordsUpdated: Boolean = false,
    val errorData: ErrorData? = null,
    val wrongProtectiveWord: Boolean = false
)