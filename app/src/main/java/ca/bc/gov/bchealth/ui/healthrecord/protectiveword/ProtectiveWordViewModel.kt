package ca.bc.gov.bchealth.ui.healthrecord.protectiveword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.common.model.ErrorData
import ca.bc.gov.repository.CacheRepository
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProtectiveWordViewModel @Inject constructor(
    private val medicationRecordRepository: MedicationRecordRepository,
    private val bcscAuthRepo: BcscAuthRepo,
    private val cacheRepository: CacheRepository,
    private val mobileConfigRepository: MobileConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FetchMedicationUiState())
    val uiState: StateFlow<FetchMedicationUiState> = _uiState.asStateFlow()

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
            val isRecordsAvailable =
                medicationRecordRepository.isMedicationRecordsAvailableForPatient(patientId)
            if (isRecordsAvailable) {
                if (isProtectiveWordValid(protectiveWord)) {
                    _uiState.update { state ->
                        state.copy(
                            isRecordsUpdated = true
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            wrongProtectiveWord = true,
                        )
                    }
                }
            } else {
                fetchMedicationRecordsFromServer(patientId, protectiveWord)
            }
        }
    }

    private suspend fun fetchMedicationRecordsFromServer(patientId: Long, protectiveWord: String) {
        _uiState.update { state ->
            state.copy(
                onLoading = true,
            )
        }
        try {
            val authParameters = bcscAuthRepo.getAuthParameters()
            val isHgServicesUp = mobileConfigRepository.getBaseUrl()
            if (isHgServicesUp) {
                medicationRecordRepository.fetchMedicationStatement(
                    patientId,
                    authParameters.first,
                    authParameters.second,
                    protectiveWord
                )
                saveProtectiveWord(protectiveWord)
                _uiState.update { state ->
                    state.copy(
                        onLoading = false,
                        isRecordsUpdated = true
                    )
                }
            } else {
                _uiState.tryEmit(
                    FetchMedicationUiState(
                        isHgServicesUp = isHgServicesUp,
                        onLoading = false
                    )
                )
            }
        } catch (e: Exception) {
            when (e) {
                is NetworkConnectionException -> {
                    _uiState.update { state ->
                        state.copy(
                            onLoading = false,
                            isConnected = false
                        )
                    }
                }
                is ProtectiveWordException -> {
                    _uiState.update { state ->
                        state.copy(
                            onLoading = false,
                            wrongProtectiveWord = true
                        )
                    }
                }
                else -> {
                    _uiState.update { state ->
                        state.copy(
                            onLoading = false,
                            errorData = ErrorData(
                                R.string.error,
                                R.string.error_message
                            ),
                        )
                    }
                }
            }
        }
    }

    fun resetUiState() {
        _uiState.update { state ->
            state.copy(
                onLoading = false,
                isRecordsUpdated = false,
                errorData = null,
                wrongProtectiveWord = false,
                isConnected = true,
                isHgServicesUp = true
            )
        }
    }

    fun updateProtectiveWordAdded(isProtectiveWordAdded: Boolean) {
        cacheRepository.updateProtectiveWordAdded(isProtectiveWordAdded)
    }
}

data class FetchMedicationUiState(
    val onLoading: Boolean = false,
    val isRecordsUpdated: Boolean = false,
    val errorData: ErrorData? = null,
    val wrongProtectiveWord: Boolean = false,
    val isConnected: Boolean = true,
    val isHgServicesUp: Boolean = true
)
