package ca.bc.gov.bchealth.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.exceptions.MustBeQueuedException
import ca.bc.gov.repository.ClearStorageRepository
import ca.bc.gov.repository.PatientWithBCSCLoginRepository
import ca.bc.gov.repository.ProfileRepository
import ca.bc.gov.repository.QueueItTokenRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import ca.bc.gov.repository.patient.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* @auther amit_metri on 05,January,2022
*/
@HiltViewModel
class BcscAuthViewModel @Inject constructor(
    private val bcscAuthRepo: BcscAuthRepo,
    private val queueItTokenRepository: QueueItTokenRepository,
    private val clearStorageRepository: ClearStorageRepository,
    private val patientWithBCSCLoginRepository: PatientWithBCSCLoginRepository,
    private val patientRepository: PatientRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _authStatus = MutableStateFlow(AuthStatus())
    val authStatus: StateFlow<AuthStatus> = _authStatus.asStateFlow()

    /*
    * Throttle calls to BCSC login
    * */
    fun verifyLoad() = viewModelScope.launch {
        try {
            _authStatus.update {
                it.copy(
                    showLoading = true
                )
            }
            val canInitiateBcscLogin = bcscAuthRepo.verifyLoad()
            _authStatus.update {
                it.copy(
                    showLoading = true,
                    canInitiateBcscLogin = canInitiateBcscLogin
                )
            }
        } catch (e: Exception) {
            when (e) {
                is MustBeQueuedException -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = true,
                            onMustBeQueued = true,
                            queItUrl = e.message,
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
        } catch (e: Exception) {
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    isError = true
                )
            }
        }
    }

    fun processAuthResponse(data: Intent?) = viewModelScope.launch {
        try {
            _authStatus.update {
                it.copy(
                    showLoading = true
                )
            }
            val isLoggedSuccess = bcscAuthRepo.processAuthResponse(data)
            if (isLoggedSuccess) {
                clearStorageRepository.clearMedicationPreferences()
                _authStatus.update {
                    it.copy(
                        showLoading = true,
                        loginStatus = LoginStatus.ACTIVE
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

    fun processLogoutResponse() = viewModelScope.launch {
        bcscAuthRepo.processLogoutResponse()
        _authStatus.update {
            it.copy(
                showLoading = false,
                loginStatus = LoginStatus.EXPIRED
            )
        }
    }

    /*
    * Check BCSC login
    * */
    fun checkLogin() = viewModelScope.launch {
        try {
            _authStatus.update {
                it.copy(
                    showLoading = true
                )
            }
            val isLoggedSuccess = bcscAuthRepo.checkLogin()
            val userName = bcscAuthRepo.getUserName()
            val loginSessionStatus = if (isLoggedSuccess) {
                LoginStatus.ACTIVE
            } else {
                LoginStatus.EXPIRED
            }
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    userName = userName,
                    loginStatus = loginSessionStatus
                )
            }
        } catch (e: Exception) {
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    loginStatus = LoginStatus.EXPIRED
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
                userName = "",
                queItTokenUpdated = false,
                loginStatus = null,
                patientId = -1L,
                isWithinAgeLimit = false,
                canInitiateBcscLogin = null,
                onMustBeQueued = false,
                tosAccepted = null
            )
        }
    }

    fun setQueItToken(token: String?) = viewModelScope.launch {
        queueItTokenRepository.setQueItToken(token)
        _authStatus.update {
            it.copy(
                showLoading = true,
                queItTokenUpdated = true,
                onMustBeQueued = false
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
            val authParameters = bcscAuthRepo.getAuthParameters()
            val isWithinAgeLimit = profileRepository.checkAgeLimit(
                authParameters.first,
                authParameters.second
            )

            if (isWithinAgeLimit) {
                _authStatus.update {
                    it.copy(
                        showLoading = true,
                        isWithinAgeLimit = true
                    )
                }
            } else {
                getEndSessionIntent()
            }
        } catch (e: Exception) {
            when (e) {
                is MustBeQueuedException -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = true,
                            onMustBeQueued = true,
                            queItUrl = e.message,
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

    fun isTermsOfServiceAccepted() = viewModelScope.launch {
        _authStatus.update {
            it.copy(
                showLoading = true
            )
        }
        try {
            val authParameters = bcscAuthRepo.getAuthParameters()
            val isTosAccepted = profileRepository.isTermsOfServiceAccepted(
                authParameters.first,
                authParameters.second
            )
            _authStatus.update {
                it.copy(
                    showLoading = true,
                    tosAccepted = if (isTosAccepted) TOSAccepted.ACCEPTED else TOSAccepted.NOT_ACCEPTED
                )
            }
        } catch (e: Exception) {
            when (e) {
                is MustBeQueuedException -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = true,
                            onMustBeQueued = true,
                            queItUrl = e.message,
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

    fun acceptTermsAndService() = viewModelScope.launch {
        _authStatus.update {
            it.copy(
                showLoading = true
            )
        }

        try {
            val authParameters = bcscAuthRepo.getAuthParameters()
            val isTosAccepted = profileRepository.acceptTermsOfService(
                authParameters.first,
                authParameters.second
            )

            if (isTosAccepted) {
                _authStatus.update {
                    it.copy(
                        showLoading = true,
                        tosAccepted = TOSAccepted.ACCEPTED
                    )
                }
            } else {
                getEndSessionIntent()
            }
        } catch (e: Exception) {
            when (e) {
                is MustBeQueuedException -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = true,
                            onMustBeQueued = true,
                            queItUrl = e.message,
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

    fun fetchPatientData() = viewModelScope.launch {
        _authStatus.update {
            it.copy(
                showLoading = true
            )
        }
        try {
            val authParameters = bcscAuthRepo.getAuthParameters()
            val patient = patientWithBCSCLoginRepository.getPatient(
                authParameters.first,
                authParameters.second
            )
            val patientId = patientRepository.insertAuthenticatedPatient(patient)
            if (patientId > 0L) {
                _authStatus.update {
                    it.copy(
                        showLoading = false,
                        patientId = patientId
                    )
                }
            }
        } catch (e: Exception) {
            when (e) {
                is MustBeQueuedException -> {
                    _authStatus.update {
                        it.copy(
                            showLoading = true,
                            onMustBeQueued = true,
                            queItUrl = e.message,
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
}

data class AuthStatus(
    val showLoading: Boolean = false,
    val authRequestIntent: Intent? = null,
    val endSessionIntent: Intent? = null,
    val isError: Boolean = false,
    val userName: String = "",
    val queItTokenUpdated: Boolean = false,
    val onMustBeQueued: Boolean = false,
    val queItUrl: String? = null,
    val loginStatus: LoginStatus? = null,
    val patientId: Long = -1L,
    val isWithinAgeLimit: Boolean = false,
    val canInitiateBcscLogin: Boolean? = null,
    val tosAccepted: TOSAccepted? = null
)

enum class LoginStatus {
    ACTIVE,
    EXPIRED
}

enum class AgeLimitCheck {
    PASSED,
    FAILED
}

enum class TOSAccepted {
    ACCEPTED,
    NOT_ACCEPTED,
    USER_DECLINED
}
