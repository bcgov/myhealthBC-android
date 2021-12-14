package ca.bc.gov.bchealth.ui.healthrecord.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            repository.fetchTestRecord(phn, dateOfBirth, collectionDate)
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
    val queItUrl: String? = null
)