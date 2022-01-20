package ca.bc.gov.bchealth.ui.healthpass.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.relation.PatientAndVaccineRecord
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.QueueItTokenRepository
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.qr.VaccineRecordState
import ca.bc.gov.repository.vaccine.VaccineDoseRepository
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
    private val patientWithVaccineRecordRepository: PatientWithVaccineRecordRepository,
    private val vaccineDoseRepository: VaccineDoseRepository
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
                FetchVaccineRecordUiState().copy(
                    onLoading = true
                )
            )

            try {
                val vaccineRecord = fetchVaccineRecordRepository.fetchVaccineRecord(
                    phn,
                    dateOfBirth,
                    dateOfVaccine
                )
                _uiState.tryEmit(
                    FetchVaccineRecordUiState().copy(
                        onLoading = false,
                        vaccineRecord = vaccineRecord
                    )
                )
            } catch (e: Exception) {
                when (e) {
                    is MustBeQueuedException -> {
                        _uiState.tryEmit(
                            FetchVaccineRecordUiState().copy(
                                onLoading = false,
                                onMustBeQueued = true,
                                queItUrl = e.message,
                            )
                        )
                    }
                    is MyHealthException -> {
                        _uiState.tryEmit(
                            FetchVaccineRecordUiState().copy(
                                isError = true,
                                errorCode = e.errCode
                            )
                        )
                    }
                }
            }
        }

    fun setQueItToken(token: String?) = viewModelScope.launch {
        Log.d(TAG, "setQueItToken: token = $token")
        queueItTokenRepository.setQueItToken(token)
        _uiState.tryEmit(
            FetchVaccineRecordUiState().copy(onLoading = false, queItTokenUpdated = true)
        )
    }

    fun getPatientWithVaccineRecord(patientId: Long) = viewModelScope.launch {
        val record = patientWithVaccineRecordRepository.getPatientWithVaccine(patientId)
        val vaccineDoses = vaccineDoseRepository.getVaccineDoses(record.vaccineRecordDto!!.id)
        record.vaccineRecordDto?.doseDtos = vaccineDoses
        _uiState.tryEmit(
            FetchVaccineRecordUiState().copy(onLoading = false, patientData = record)
        )
    }
}

data class FetchVaccineRecordUiState(
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val patientData: PatientAndVaccineRecord? = null,
    val vaccineRecord: Pair<VaccineRecordState, PatientVaccineRecord?>? = null,
    val isError: Boolean = false,
    val errorCode: Int = 0
)
