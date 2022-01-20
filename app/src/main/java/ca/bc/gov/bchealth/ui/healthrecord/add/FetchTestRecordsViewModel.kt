package ca.bc.gov.bchealth.ui.healthrecord.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.ErrorData
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.QueueItTokenRepository
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
class FetchTestRecordsViewModel @Inject constructor(
    private val queueItTokenRepository: QueueItTokenRepository,
    private val repository: FetchTestResultRepository
) : ViewModel() {

    private val _uiState =
        MutableSharedFlow<FetchTestRecordUiState>(replay = 0, extraBufferCapacity = 1)
    val uiState: SharedFlow<FetchTestRecordUiState> = _uiState.asSharedFlow()

    companion object {
        private const val TAG = "FetchTestRecordsViewModel"
    }

    fun fetchTestRecord(phn: String, dateOfBirth: String, collectionDate: String) =
        viewModelScope.launch {

            _uiState.tryEmit(
                FetchTestRecordUiState(
                    onLoading = true
                )
            )

            try {
                val tesTestResultId = repository.fetchTestRecord(phn, dateOfBirth, collectionDate)
                _uiState.tryEmit(
                    FetchTestRecordUiState(
                        onTestResultFetched = tesTestResultId
                    )
                )
            } catch (e: Exception) {
                when (e) {
                    is MustBeQueuedException -> {
                        _uiState.tryEmit(
                            FetchTestRecordUiState(
                                onLoading = true,
                                queItTokenUpdated = false,
                                onMustBeQueued = true,
                                queItUrl = e.message
                            )
                        )
                    }
                    is MyHealthException -> {
                        if (e.errCode == SERVER_ERROR_DATA_MISMATCH) {
                            _uiState.tryEmit(
                                FetchTestRecordUiState(
                                    errorData = ErrorData(
                                        R.string.error_data_mismatch_title,
                                        R.string.error_test_result_data_mismatch_message
                                    )
                                )
                            )
                        } else {
                            _uiState.tryEmit(
                                FetchTestRecordUiState(
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

    fun setQueItToken(token: String?) = viewModelScope.launch {
        Log.d(TAG, "setQueItToken: token = $token")
        queueItTokenRepository.setQueItToken(token)
        _uiState.tryEmit(
            FetchTestRecordUiState(
                onLoading = false, queItTokenUpdated = true
            )
        )
    }
}

data class FetchTestRecordUiState(
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val onTestResultFetched: Long = -1L,
    val isError: Boolean = false,
    val errorData: ErrorData? = null
)
