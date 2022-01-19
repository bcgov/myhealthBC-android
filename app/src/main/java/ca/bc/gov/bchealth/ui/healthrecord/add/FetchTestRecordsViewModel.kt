package ca.bc.gov.bchealth.ui.healthrecord.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.QueueItTokenRepository
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
class FetchTestRecordsViewModel @Inject constructor(
    private val queueItTokenRepository: QueueItTokenRepository,
    private val repository: FetchTestResultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FetchTestRecordUiState())
    val uiState: StateFlow<FetchTestRecordUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "FetchTestRecordsViewModel"
    }

    fun fetchTestRecord(phn: String, dateOfBirth: String, collectionDate: String) =
        viewModelScope.launch {

            _uiState.update { fetchTestRecordUiState ->
                fetchTestRecordUiState.copy(
                    onLoading = true,
                    queItTokenUpdated = false,
                    onMustBeQueued = false,
                    queItUrl = null,
                    onTestResultFetched = 0L,
                    isError = false
                )
            }

            try {
                val tesTestResultId = repository.fetchTestRecord(phn, dateOfBirth, collectionDate)
                _uiState.update { fetchTestRecordUiState ->
                    fetchTestRecordUiState.copy(
                        onLoading = false,
                        queItTokenUpdated = false,
                        onMustBeQueued = false,
                        queItUrl = null,
                        onTestResultFetched = tesTestResultId,
                        isError = false
                    )
                }
            } catch (e: Exception) {
                when (e) {
                    is MustBeQueuedException -> {
                        _uiState.update {
                            it.copy(
                                onLoading = true,
                                queItTokenUpdated = false,
                                onMustBeQueued = true,
                                queItUrl = e.message,
                                onTestResultFetched = 0L,
                                isError = false
                            )
                        }
                    }
                    is MyHealthException -> {
                        _uiState.update {
                            it.copy(
                                onLoading = false,
                                queItTokenUpdated = false,
                                onMustBeQueued = false,
                                queItUrl = null,
                                onTestResultFetched = 0L,
                                isError = true
                            )
                        }
                    }
                }
            }
        }

    fun setQueItToken(token: String?) = viewModelScope.launch {
        Log.d(TAG, "setQueItToken: token = $token")
        queueItTokenRepository.setQueItToken(token)
        _uiState.update {
            it.copy(onLoading = false, queItTokenUpdated = true)
        }
    }
}

data class FetchTestRecordUiState(
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val onTestResultFetched: Long = -1L,
    val isError: Boolean = false
)
