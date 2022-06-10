package ca.bc.gov.bchealth.ui.healthpass.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.const.SERVER_ERROR_INCORRECT_PHN
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.model.ErrorData
import ca.bc.gov.common.model.relation.PatientWithVaccineAndDosesDto
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.QueueItTokenRepository
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.qr.VaccineRecordState
import ca.bc.gov.repository.worker.MobileConfigRepository
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
class FetchVaccineRecordViewModel @Inject constructor(
    private val queueItTokenRepository: QueueItTokenRepository,
    private val fetchVaccineRecordRepository: FetchVaccineRecordRepository,
    private val patientRepository: PatientRepository,
    private val mobileConfigRepository: MobileConfigRepository
) : ViewModel() {

    companion object {
        private const val TAG = "FetchVaccineRecordViewModel"
    }

    private val _uiState =
        MutableSharedFlow<FetchVaccineRecordUiState>(replay = 0, extraBufferCapacity = 1)
    val uiState: SharedFlow<FetchVaccineRecordUiState> = _uiState.asSharedFlow()

    fun fetchVaccineRecord(phn: String, dateOfBirth: String, dateOfVaccine: String) =
        viewModelScope.launch {

            _uiState.tryEmit(
                FetchVaccineRecordUiState(
                    onLoading = true
                )
            )

            try {
                val isHgServicesUp = mobileConfigRepository.getBaseUrl()

                if (isHgServicesUp) {
                    val vaccineRecord = fetchVaccineRecordRepository.fetchVaccineRecord(
                        phn,
                        dateOfBirth,
                        dateOfVaccine
                    )
                    _uiState.tryEmit(
                        FetchVaccineRecordUiState(
                            onLoading = false,
                            vaccineRecord = vaccineRecord
                        )
                    )
                } else {
                    _uiState.tryEmit(
                        FetchVaccineRecordUiState(
                            isHgServicesUp = isHgServicesUp,
                            onLoading = false
                        )
                    )
                }
            } catch (e: Exception) {
                when (e) {
                    is NetworkConnectionException -> {
                        _uiState.tryEmit(
                            FetchVaccineRecordUiState(
                                onLoading = false,
                                isConnected = false
                            )
                        )
                    }
                    is MustBeQueuedException -> {
                        _uiState.tryEmit(
                            FetchVaccineRecordUiState(
                                onLoading = true,
                                onMustBeQueued = true,
                                queItUrl = e.message,
                            )
                        )
                    }
                    is MyHealthException -> {
                        when (e.errCode) {
                            SERVER_ERROR_DATA_MISMATCH, SERVER_ERROR_INCORRECT_PHN -> {
                                _uiState.tryEmit(
                                    FetchVaccineRecordUiState(
                                        errorData = ErrorData(
                                            R.string.error_data_mismatch_title,
                                            R.string.error_vaccine_data_mismatch_message
                                        )
                                    )
                                )
                            }
                            else -> {
                                _uiState.tryEmit(
                                    FetchVaccineRecordUiState(
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

    fun setQueItToken(token: String?) = viewModelScope.launch {
        Log.d(TAG, "setQueItToken: token = $token")
        queueItTokenRepository.setQueItToken(token)
        _uiState.tryEmit(
            FetchVaccineRecordUiState(onLoading = false, queItTokenUpdated = true)
        )
    }

    fun getPatientWithVaccineRecord(patientId: Long) = viewModelScope.launch {
        val record = patientRepository.getPatientWithVaccineAndDoses(patientId)
        _uiState.tryEmit(
            FetchVaccineRecordUiState(onLoading = false, patientDataDto = record)
        )
    }

    fun resetUiState() {
        _uiState.tryEmit(
            FetchVaccineRecordUiState(
                isHgServicesUp = null,
                onLoading = false,
                queItTokenUpdated = false,
                onMustBeQueued = false,
                queItUrl = null,
                patientDataDto = null,
                vaccineRecord = null,
                errorData = null,
                isConnected = true
            )
        )
    }
}

data class FetchVaccineRecordUiState(
    val isHgServicesUp: Boolean? = null,
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val patientDataDto: PatientWithVaccineAndDosesDto? = null,
    val vaccineRecord: Pair<VaccineRecordState, PatientVaccineRecord?>? = null,
    val errorData: ErrorData? = null,
    val isConnected: Boolean = true
)
