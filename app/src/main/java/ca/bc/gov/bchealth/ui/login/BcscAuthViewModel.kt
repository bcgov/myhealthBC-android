package ca.bc.gov.bchealth.ui.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import ca.bc.gov.bchealth.workers.WorkerInvoker
import ca.bc.gov.common.const.MUST_CALL_MOBILE_CONFIG
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.exceptions.NetworkConnectionException
import ca.bc.gov.common.exceptions.ServiceDownException
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.UserAuthenticationStatus
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.utils.toUniquePatientName
import ca.bc.gov.repository.CacheRepository
import ca.bc.gov.repository.ClearStorageRepository
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.UserProfileRepository
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.bcsc.PostLoginCheck
import ca.bc.gov.repository.patient.PatientRepository
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* @auther amit_metri on 05,January,2022
*/
@HiltViewModel
class BcscAuthViewModel @Inject constructor(
    private val bcscAuthRepo: BcscAuthRepo,
    private val workerInvoker: WorkerInvoker,
    private val clearStorageRepository: ClearStorageRepository,
    private val userProfileRepository: UserProfileRepository,
    private val patientRepository: PatientRepository,
    private val patientWithBCSCLoginRepository: PatientWithBCSCLoginRepository,
    private val mobileConfigRepository: MobileConfigRepository,
    private val cacheRepository: CacheRepository
) : ViewModel() {

    private val _authStatus = MutableStateFlow(AuthStatus())
    val authStatus: StateFlow<AuthStatus> = _authStatus.asStateFlow()

    val userAuthenticationState =
        bcscAuthRepo.userAuthenticationStatus.catch { excepton -> emit(UserAuthenticationStatus.UN_AUTHENTICATED) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = UserAuthenticationStatus.UN_AUTHENTICATED
        )

    /*
    * Throttle calls to BCSC login
    * */
    fun verifyLoad() = viewModelScope.launch {
        try {
            _authStatus.update { it.copy(showLoading = true) }

            mobileConfigRepository.refreshMobileConfiguration()
            _authStatus.update {
                it.copy(
                    showLoading = true,
                    canInitiateBcscLogin = true
                )
            }
        } catch (e: Exception) {
            when (e) {
                is NetworkConnectionException -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = false,
                            isConnected = false
                        )
                    }
                }

                is ServiceDownException -> _authStatus.update {
                    it.copy(
                        showLoading = true,
                        canInitiateBcscLogin = false
                    )
                }

                else -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = false,
                            isError = true
                        )
                    }
                }
            }
        }
    }

    /*
    * BCSC Login
    * */
    fun initiateLogin() = viewModelScope.launch {
        try {
            _authStatus.update {
                it.copy(
                    showLoading = true
                )
            }
            val authRequestIntent = bcscAuthRepo.initiateLogin()
            _authStatus.update {
                it.copy(
                    showLoading = true,
                    authRequestIntent = authRequestIntent
                )
            }
        } catch (e: MyHealthException) {
            when (e.errCode) {
                MUST_CALL_MOBILE_CONFIG -> callMobileConfig()
                else -> _authStatus.update {
                    it.copy(
                        showLoading = false,
                        isError = true
                    )
                }
            }
        } catch (e: Exception) {
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    isError = true
                )
            }
        }
    }

    private suspend fun callMobileConfig() {
        try {
            mobileConfigRepository.refreshMobileConfiguration()
        } catch (e: Exception) {
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    isError = true
                )
            }
            return
        }
        initiateLogin()
    }

    fun processAuthResponse(data: Intent?, context: Context) = viewModelScope.launch {
        try {
            _authStatus.update {
                it.copy(
                    showLoading = true
                )
            }
            val isLoggedSuccess = bcscAuthRepo.processAuthResponse(data)
            if (isLoggedSuccess) {
                // clear medication pref
                cacheRepository.updateProtectiveWordAdded(false)
                clearStorageRepository.clearMedicationPreferences()

                _authStatus.update {
                    it.copy(
                        showLoading = true,
                        loginStatus = LoginStatus.ACTIVE
                    )
                }
                setPostLoginCheck(PostLoginCheck.IN_PROGRESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    isError = true
                )
            }
        }
    }

    /*
    * BCSC logout
    * */
    fun getEndSessionIntent() = viewModelScope.launch {
        try {
            _authStatus.update {
                it.copy(
                    showLoading = true
                )
            }
            val endSessionIntent = bcscAuthRepo.getEndSessionIntent()
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    endSessionIntent = endSessionIntent
                )
            }
        } catch (e: Exception) {
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    isError = true
                )
            }
        }
    }

    fun processLogoutResponse(context: Context) = viewModelScope.launch {
        // cancel work manager
        WorkManager.getInstance(context).cancelUniqueWork(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME)
        bcscAuthRepo.processLogoutResponse()
        _authStatus.update {
            it.copy(
                showLoading = false,
                loginStatus = LoginStatus.EXPIRED
            )
        }
        setPostLoginCheck(PostLoginCheck.COMPLETE)
    }

    /*
    * Check BCSC login
    * */
    fun checkSession() = viewModelScope.launch {
        _authStatus.update {
            it.copy(
                showLoading = true
            )
        }
        val isLoggedSuccess = bcscAuthRepo.checkSession()
        var userName: String? = null
        try {
            val patient =
                patientRepository.findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
            userName = patient.fullName
            val loginSessionStatus = if (isLoggedSuccess) {
                LoginStatus.ACTIVE
            } else {
                LoginStatus.EXPIRED
            }
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    userName = userName,
                    loginStatus = loginSessionStatus,
                    patient = patient
                )
            }
        } catch (e: Exception) {
            // no implementation required.

            _authStatus.update {
                it.copy(
                    showLoading = false,
                    userName = userName,
                    loginStatus = LoginStatus.NOT_AUTHENTICATED
                )
            }
        }
    }

    fun resetAuthStatus() {
        _authStatus.update {
            it.copy(
                showLoading = false,
                authRequestIntent = null,
                endSessionIntent = null,
                isError = false,
                userName = null,
                queItTokenUpdated = false,
                loginStatus = null,
                ageLimitCheck = null,
                canInitiateBcscLogin = null,
                tosStatus = null,
                queItUrl = null,
                isConnected = true
            )
        }
    }

    fun checkAgeLimit() = viewModelScope.launch {
        _authStatus.update {
            it.copy(
                showLoading = true
            )
        }
        try {
            val authParameters = bcscAuthRepo.getAuthParametersDto()
            val isWithinAgeLimit = userProfileRepository.checkAgeLimit(
                authParameters.token,
                authParameters.hdid
            )

            _authStatus.update {
                it.copy(
                    showLoading = true,
                    ageLimitCheck = if (isWithinAgeLimit) AgeLimitCheck.PASSED else AgeLimitCheck.FAILED
                )
            }
        } catch (e: Exception) {
            when (e) {
                is NetworkConnectionException -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = false,
                            isConnected = false
                        )
                    }
                }

                else -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = false,
                            isError = true
                        )
                    }
                }
            }
        }
    }

    fun checkTermsOfServiceStatus() = viewModelScope.launch {
        _authStatus.update {
            it.copy(
                showLoading = true
            )
        }
        try {
            val authParameters = bcscAuthRepo.getAuthParametersDto()
            val userProfileDto = userProfileRepository.checkForTermsOfServices(
                authParameters.token,
                authParameters.hdid
            )

            val tosStatus = when {
                userProfileDto.acceptedTermsOfService && !userProfileDto.hasTermsOfServiceUpdated -> TOSStatus.ACCEPTED
                userProfileDto.acceptedTermsOfService && userProfileDto.hasTermsOfServiceUpdated -> TOSStatus.UPDATED
                else -> TOSStatus.NOT_ACCEPTED
            }

            performPatientDetailCheck(authParameters)

            _authStatus.update {
                it.copy(
                    showLoading = true,
                    tosStatus = tosStatus
                )
            }
        } catch (e: Exception) {
            when (e) {
                is NetworkConnectionException -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = false,
                            isConnected = false
                        )
                    }
                }

                else -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = false,
                            isError = true
                        )
                    }
                }
            }
        }
    }

    private suspend fun performPatientDetailCheck(authParameters: AuthParametersDto) {
        var patientFromRemoteSource: PatientDto? = null
        try {
            patientFromRemoteSource = patientWithBCSCLoginRepository.getPatient(
                authParameters.token,
                authParameters.hdid
            )
            val patientFromLocalSource = patientRepository
                .findPatientByAuthStatus(AuthenticationStatus.AUTHENTICATED)
            /*
            * If user logs in using different user credentials post session expiry,
            * exiting BCSC user is deleted and new BCSC user details inserted immediately.
            * */
            if (!patientFromLocalSource.fullName.toUniquePatientName()
                .equals(
                        patientFromRemoteSource.fullName.toUniquePatientName(),
                        true
                    )
            ) {
                patientRepository.deleteByPatientId(patientFromLocalSource.id)
                patientRepository.insertAuthenticatedPatient(patientFromRemoteSource)
            }
        } catch (e: java.lang.Exception) {
            /*
            * If AUTHENTICATED USER is not found patient details are inserted at this stage
            * to show patient name soon after login
            * */
            patientFromRemoteSource?.let { patientRepository.insertAuthenticatedPatient(it) }
        }
    }

    fun acceptTermsAndService(termsOfServiceId: String, hasTOSUpdated: Boolean = false) = viewModelScope.launch {
        _authStatus.update {
            it.copy(
                showLoading = true
            )
        }

        try {
            val authParameters = bcscAuthRepo.getAuthParametersDto()
            val isTosAccepted = userProfileRepository.acceptTermsOfService(
                authParameters.token,
                authParameters.hdid,
                termsOfServiceId,
                hasTOSUpdated
            )

            if (isTosAccepted) {
                _authStatus.update {
                    it.copy(
                        showLoading = true,
                        tosStatus = TOSStatus.ACCEPTED
                    )
                }
            } else {
                getEndSessionIntent()
            }
        } catch (e: Exception) {
            when (e) {
                is NetworkConnectionException -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = false,
                            isConnected = false
                        )
                    }
                }

                else -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = false,
                            isError = true
                        )
                    }
                }
            }
        }
    }

    fun setPostLoginCheck(postLoginCheck: PostLoginCheck) {
        bcscAuthRepo.setPostLoginCheck(postLoginCheck)
    }

    fun executeOneTimeDataFetch() = workerInvoker.executeOneTimeDataFetch()
}

data class AuthStatus(
    val showLoading: Boolean = false,
    val authRequestIntent: Intent? = null,
    val endSessionIntent: Intent? = null,
    val isError: Boolean = false,
    val userName: String? = null,
    val queItTokenUpdated: Boolean = false,
    val queItUrl: String? = null,
    val loginStatus: LoginStatus? = null,
    val ageLimitCheck: AgeLimitCheck? = null,
    val canInitiateBcscLogin: Boolean? = null,
    val tosStatus: TOSStatus? = null,
    val isConnected: Boolean = true,
    val patient: PatientDto? = null
)

enum class LoginStatus {
    ACTIVE,
    EXPIRED,
    NOT_AUTHENTICATED
}

enum class AgeLimitCheck {
    PASSED,
    FAILED
}

enum class TOSStatus {
    ACCEPTED,
    UPDATED,
    NOT_ACCEPTED,
    USER_DECLINED
}
