package ca.bc.gov.bchealth.ui.healthpass.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.repository.FetchVaccineRecordRepository
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
class FetchVaccineRecordViewModel @Inject constructor(
    private val queueItTokenRepository: QueueItTokenRepository,
    private val fetchVaccineRecordRepository: FetchVaccineRecordRepository
) : ViewModel() {

    companion object {
        private const val TAG = "FetchVaccineRecordViewModel"
    }

    private val _uiState = MutableStateFlow(FetchVaccineRecordUiState())
    val uiState: StateFlow<FetchVaccineRecordUiState> = _uiState.asStateFlow()

    fun fetchVaccineRecord(phn: String, dateOfBirth: String, dateOfVaccine: String) =
        viewModelScope.launch {

            try {
                fetchVaccineRecordRepository.fetchVaccineRecord(
                    phn,
                    dateOfBirth,
                    dateOfVaccine
                )
            } catch (e: Exception) {
                when (e) {
                    is MustBeQueuedException -> {
                        _uiState.update {
                            it.copy(
                                onLoading = false,
                                queItTokenUpdated = false,
                                onMustBeQueued = true,
                                queItUrl = e.message
                            )
                        }
                    }
                    else -> {
                        //TODO: Emit Error state
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

data class FetchVaccineRecordUiState(
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null
)