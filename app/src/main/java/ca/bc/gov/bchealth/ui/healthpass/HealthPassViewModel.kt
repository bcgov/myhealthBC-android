package ca.bc.gov.bchealth.ui.healthpass

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.model.mapper.toUiModel
import ca.bc.gov.bchealth.ui.healthpass.add.FetchVaccineRecordUiState
import ca.bc.gov.common.const.SERVER_ERROR_DATA_MISMATCH
import ca.bc.gov.common.const.SERVER_ERROR_INCORRECT_PHN
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.common.exceptions.MyHealthNetworkException
import ca.bc.gov.common.model.AuthenticatedCovidTestDto
import ca.bc.gov.common.model.ErrorData
import ca.bc.gov.common.model.PatientWithBCSCLoginDto
import ca.bc.gov.common.model.relation.PatientWithVaccineRecordDto
import ca.bc.gov.repository.FetchTestResultRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.OnBoardingRepository
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.PatientWithVaccineRecordRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.qr.VaccineRecordState
import ca.bc.gov.repository.vaccine.VaccineRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class HealthPassViewModel @Inject constructor(
    private val repository: PatientWithVaccineRecordRepository,
    private val vaccineRecordRepository: VaccineRecordRepository,
    private val onBoardingRepository: OnBoardingRepository,
    private val patientWithBCSCLoginRepository: PatientWithBCSCLoginRepository,
    private val bcscAuthRepo: BcscAuthRepo,
    private val fetchVaccineRecordRepository: FetchVaccineRecordRepository,
    private val fetchTestResultRepository: FetchTestResultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthPassUiState())
    val uiState: StateFlow<HealthPassUiState> = _uiState.asStateFlow()
    var isAuthenticationRequired: Boolean = true
    var isBcscLoginRequiredPostBiometrics: Boolean = false
    val healthPasses = repository.patientsVaccineRecord.map { records ->
        records.map { record ->
            record.toUiModel()
        }
    }

    private val _fetchPatientUiState =
        MutableSharedFlow<FetchPatientUiState>(replay = 0, extraBufferCapacity = 1)
    val fetchPatientUiState: SharedFlow<FetchPatientUiState> = _fetchPatientUiState.asSharedFlow()

    private val _fetchVaccineRecordUiState =
        MutableSharedFlow<FetchVaccineRecordUiState>(replay = 0, extraBufferCapacity = 1)
    val fetchVaccineRecordUiState: SharedFlow<FetchVaccineRecordUiState> = _fetchVaccineRecordUiState.asSharedFlow()

    private val _fetchLabTestUiState =
        MutableSharedFlow<FetchTestRecordUiState>(replay = 0, extraBufferCapacity = 1)
    val fetchLabTestUiState: SharedFlow<FetchTestRecordUiState> = _fetchLabTestUiState.asSharedFlow()

    fun launchCheck() = viewModelScope.launch {
        when {
            onBoardingRepository.onBoardingRequired -> {
                isBcscLoginRequiredPostBiometrics = true
                _uiState.update { state ->
                    state.copy(isLoading = false, isOnBoardingRequired = true)
                }
            }
            isAuthenticationRequired -> {
                _uiState.update { state -> state.copy(isAuthenticationRequired = true) }
            }
            isBcscLoginRequiredPostBiometrics -> {
                _uiState.update { state -> state.copy(isBcscLoginRequiredPostBiometrics = true) }
            }
        }
    }

    fun onBoardingShown() {
        _uiState.update {
            it.copy(isOnBoardingRequired = false)
        }
    }

    fun onAuthenticationRequired(isRequired: Boolean) {
        isAuthenticationRequired = isRequired
        _uiState.update { state -> state.copy(isAuthenticationRequired = isRequired) }
    }

    fun onBcscLoginRequired(isRequired: Boolean) {
        isBcscLoginRequiredPostBiometrics = isRequired
        _uiState.update { state -> state.copy(isBcscLoginRequiredPostBiometrics = isRequired) }
    }

    fun deleteHealthPass(vaccineRecordId: Long) = viewModelScope.launch {
        vaccineRecordRepository.delete(vaccineRecordId = vaccineRecordId)
    }

    fun updateHealthPassOrder(healthPasses: List<HealthPass>) =
        viewModelScope.launch {
            repository.updatePatientOrder(
                healthPasses.mapIndexed { index, healthPass ->
                    healthPass.patientId to index.toLong()
                }
            )
        }

    fun fetchPatient() =
        viewModelScope.launch {

            _fetchPatientUiState.tryEmit(
                FetchPatientUiState(
                    onLoading = true
                )
            )

            try {
                val pair: Pair<String, String> = bcscAuthRepo.getHdId()
                val patient = patientWithBCSCLoginRepository.getPatient(pair.first, pair.second)
                _fetchPatientUiState.tryEmit(
                    FetchPatientUiState(
                        onLoading = false,
                        patient = patient
                    )
                )
            } catch (e: Exception) {
                when (e) {
                    is MyHealthNetworkException -> {
                        _fetchPatientUiState.tryEmit(
                            FetchPatientUiState(
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

    fun fetchAuthenticatedVaccineRecord() =
        viewModelScope.launch {

            _fetchVaccineRecordUiState.tryEmit(
                FetchVaccineRecordUiState(
                    onLoading = true
                )
            )

            try {
                val pair = bcscAuthRepo.getHdId()
                val vaccineRecord = fetchVaccineRecordRepository.fetchAuthenticatedVaccineRecord(
                    token = pair.first, hdid = pair.second
                )
                _fetchVaccineRecordUiState.tryEmit(
                    FetchVaccineRecordUiState(
                        onLoading = false,
                        vaccineRecord = vaccineRecord
                    )
                )
            } catch (e: Exception) {
                when (e) {
                    is MyHealthNetworkException -> {
                        when (e.errCode) {
                            SERVER_ERROR_DATA_MISMATCH -> {
                                _fetchVaccineRecordUiState.tryEmit(
                                    FetchVaccineRecordUiState(
                                        errorData = ErrorData(
                                            R.string.error_data_mismatch_title,
                                            R.string.error_vaccine_data_mismatch_message
                                        )
                                    )
                                )
                            }
                            SERVER_ERROR_INCORRECT_PHN -> {
                                _fetchVaccineRecordUiState.tryEmit(
                                    FetchVaccineRecordUiState(
                                        errorData = ErrorData(
                                            R.string.error,
                                            R.string.error_incorrect_phn
                                        )
                                    )
                                )
                            }
                            else -> {
                                _fetchVaccineRecordUiState.tryEmit(
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

    fun fetchAuthenticatedTestRecord() =
        viewModelScope.launch {

            _fetchLabTestUiState.tryEmit(
                FetchTestRecordUiState(
                    onLoading = true
                )
            )

            try {
                val pair = bcscAuthRepo.getHdId()
                val response = fetchTestResultRepository.fetchAuthenticatedTestRecord(pair.first, pair.second)
                _fetchLabTestUiState.tryEmit(
                    FetchTestRecordUiState(
                        payload = response
                    )
                )
            } catch (e: Exception) {
                when (e) {
                    is MustBeQueuedException -> {
                        _fetchLabTestUiState.tryEmit(
                            FetchTestRecordUiState(
                                onLoading = true,
                                queItTokenUpdated = false,
                                onMustBeQueued = true,
                                queItUrl = e.message
                            )
                        )
                    }
                    is MyHealthNetworkException -> {
                        when (e.errCode) {
                            SERVER_ERROR_DATA_MISMATCH -> {
                                _fetchLabTestUiState.tryEmit(
                                    FetchTestRecordUiState(
                                        errorData = ErrorData(
                                            R.string.error_data_mismatch_title,
                                            R.string.error_test_result_data_mismatch_message
                                        )
                                    )
                                )
                            }
                            SERVER_ERROR_INCORRECT_PHN -> {
                                _fetchLabTestUiState.tryEmit(
                                    FetchTestRecordUiState(
                                        errorData = ErrorData(
                                            R.string.error,
                                            R.string.error_incorrect_phn
                                        )
                                    )
                                )
                            }
                            else -> {
                                _fetchLabTestUiState.tryEmit(
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
        }
}

data class HealthPassUiState(
    val isLoading: Boolean = false,
    val isOnBoardingRequired: Boolean = false,
    val isAuthenticationRequired: Boolean = false,
    val isBcscLoginRequiredPostBiometrics: Boolean = false
)

data class HealthPass(
    val patientId: Long,
    val vaccineRecordId: Long,
    var isExpanded: Boolean = true,
    val name: String,
    val qrIssuedDate: String?,
    val shcUri: String,
    val qrCode: Bitmap?,
    val federalTravelPassState: FederalTravelPassState,
    val state: PassState
)

data class PassState(
    val color: Int,
    val status: Int,
    val icon: Int
)

data class FederalTravelPassState(
    val title: Int,
    val icon: Int,
    val pdf: String?
)

data class FetchPatientUiState(
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val patientDataDto: PatientWithVaccineRecordDto? = null,
    val patient: PatientWithBCSCLoginDto? = null,
    val errorData: ErrorData? = null
)
data class FetchVaccineRecordUiState(
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val patientDataDto: PatientWithVaccineRecordDto? = null,
    val vaccineRecord: Pair<VaccineRecordState, PatientVaccineRecord?>? = null,
    val errorData: ErrorData? = null
)
data class FetchTestRecordUiState(
    val onLoading: Boolean = false,
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val payload: AuthenticatedCovidTestDto? = null,
    val isError: Boolean = false,
    val errorData: ErrorData? = null
)