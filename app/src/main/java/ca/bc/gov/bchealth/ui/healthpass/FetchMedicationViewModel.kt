package ca.bc.gov.bchealth.ui.healthpass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.const.AUTH_ERROR_DO_LOGIN
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import ca.bc.gov.common.model.ErrorData
import ca.bc.gov.repository.MedicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 08,February,2022
*/
@HiltViewModel
class FetchMedicationViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationUiState())
    val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()

    fun fetchMedicationStatement() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        try {
            medicationRepository.fetchMedicationStatement()
        } catch (e: Exception) {
            when (e) {
                is MustBeQueuedException -> {
                    _uiState.tryEmit(
                        MedicationUiState(
                            isLoading = true,
                            onMustBeQueued = true,
                            queItUrl = e.message,
                        )
                    )
                }
                is MyHealthNetworkException -> {
                    _uiState.tryEmit(
                        MedicationUiState(
                            errorData = ErrorData(
                                R.string.error,
                                R.string.error_message
                            )
                        )
                    )
                }
                is MyHealthException -> {
                    when (e.errCode) {
                        AUTH_ERROR_DO_LOGIN -> {
                            _uiState.tryEmit(
                                MedicationUiState(
                                    isLoginRequired = true
                                )
                            )
                        }
                        else -> {
                            _uiState.tryEmit(
                                MedicationUiState(
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

data class MedicationUiState(
    val isLoading: Boolean = false,
    val isLoginRequired: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val errorData: ErrorData? = null
)
