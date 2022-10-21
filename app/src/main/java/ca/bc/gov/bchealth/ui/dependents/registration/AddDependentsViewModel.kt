package ca.bc.gov.bchealth.ui.dependents.registration

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.const.SERVER_ERROR_INCORRECT_PHN
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.model.ErrorData
import ca.bc.gov.repository.DependentsRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDependentsViewModel @Inject constructor(
    private val dependentsRepository: DependentsRepository,
    private val mobileConfigRepository: MobileConfigRepository
) : ViewModel() {

    private val _uiState = MutableSharedFlow<AddDependentsUiState>(extraBufferCapacity = 1)
    val uiState: SharedFlow<AddDependentsUiState> = _uiState.asSharedFlow()

    fun registerDependent(
        firstName: String,
        lastName: String,
        dob: String,
        phn: String,
    ) {
        viewModelScope.launch {
            emitLoading(true)
            try {
                val isHgServicesUp = mobileConfigRepository.getBaseUrl()

                if (isHgServicesUp) {
                    dependentsRepository.addDependent(firstName, lastName, dob, phn)
                    emitLoading(false)
                } else {
                    _uiState.tryEmit(
                        AddDependentsUiState(isHgServicesUp = false, onLoading = false)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is NetworkConnectionException -> {
                        _uiState.tryEmit(
                            AddDependentsUiState(
                                onLoading = false,
                                isConnected = false
                            )
                        )
                    }
                    is MyHealthException -> {
                        when (e.errCode) {
                            SERVER_ERROR_DATA_MISMATCH, SERVER_ERROR_INCORRECT_PHN -> emitError(
                                R.string.error_data_mismatch_title,
                                R.string.error_vaccine_data_mismatch_message
                            )
                            else -> emitError()
                        }
                    }

                    else -> emitError()
                }
            }
        }
    }

    private fun emitLoading(state: Boolean) {
        _uiState.tryEmit(AddDependentsUiState(onLoading = state))
    }

    private fun emitError(
        @StringRes title: Int = R.string.error,
        @StringRes message: Int = R.string.error_message
    ) = _uiState.tryEmit(
        AddDependentsUiState(
            errorData = ErrorData(title, message)
        )
    )

    fun resetUiState() {
        _uiState.tryEmit(
            AddDependentsUiState(
                isHgServicesUp = true,
                onLoading = false,
                errorData = null,
                isConnected = true,
            )
        )
    }
}

data class AddDependentsUiState(
    val isHgServicesUp: Boolean = true,
    val onLoading: Boolean = false,
    val errorData: ErrorData? = null,
    val isConnected: Boolean = true,
)
