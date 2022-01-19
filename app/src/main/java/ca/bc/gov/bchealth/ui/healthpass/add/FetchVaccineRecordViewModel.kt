package ca.bc.gov.bchealth.ui.healthpass.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.model.relation.PatientAndVaccineRecord
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.QueueItTokenRepository
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.qr.VaccineRecordState
import ca.bc.gov.repository.vaccine.VaccineDoseRepository
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
    private val fetchVaccineRecordRepository: FetchVaccineRecordRepository,
    private val patientWithVaccineRecordRepository: PatientWithVaccineRecordRepository,
    private val vaccineDoseRepository: VaccineDoseRepository
) : ViewModel() {

    companion object {
        private const val TAG = "FetchVaccineRecordViewModel"
    }

    private val _uiState = MutableStateFlow(FetchVaccineRecordUiState())
    val uiState: StateFlow<FetchVaccineRecordUiState> = _uiState.asStateFlow()

    fun fetchVaccineRecord(phn: String, dateOfBirth: String, dateOfVaccine: String) =
        viewModelScope.launch {

            _uiState.update { fetchTestRecordUiState ->
                fetchTestRecordUiState.copy(
                    onLoading = true,
                    queItTokenUpdated = false,
                    onMustBeQueued = false,
                    queItUrl = null,
                    vaccineRecord = null,
                    isError = false
                )
            }

            try {
                val vaccineRecord = fetchVaccineRecordRepository.fetchVaccineRecord(
                    phn,
                    dateOfBirth,
                    dateOfVaccine
                )
                _uiState.update { fetchVaccineRecordUiState ->
                    fetchVaccineRecordUiState.copy(
                        onLoading = false,
                        queItTokenUpdated = false,
                        onMustBeQueued = false,
                        queItUrl = null,
                        vaccineRecord = vaccineRecord,
                        isError = false
                    )
                }
            } catch (e: Exception) {
                when (e) {
                    is MustBeQueuedException -> {
                        _uiState.update {
                            it.copy(
                                onLoading = false,
                                queItTokenUpdated = false,
                                onMustBeQueued = true,
                                queItUrl = e.message,
                                vaccineRecord = null,
                                isError = false
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                onLoading = false,
                                queItTokenUpdated = false,
                                onMustBeQueued = false,
                                queItUrl = null,
                                vaccineRecord = null,
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

    fun getPatientWithVaccineRecord(patientId: Long) = viewModelScope.launch {
        val record = patientWithVaccineRecordRepository.getPatientWithVaccine(patientId)
        val vaccineDoses = vaccineDoseRepository.getVaccineDoses(record.vaccineRecord!!.id)
        record.vaccineRecord?.doses = vaccineDoses
        _uiState.update {
            it.copy(onLoading = false, patientData = record)
        }
    }
}

data class FetchVaccineRecordUiState(
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val patientData: PatientAndVaccineRecord? = null,
    val vaccineRecord: Pair<VaccineRecordState, PatientVaccineRecord?>? = null,
    val isError: Boolean = false
)
