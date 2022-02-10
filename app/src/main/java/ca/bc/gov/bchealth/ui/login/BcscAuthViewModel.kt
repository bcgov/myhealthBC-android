package ca.bc.gov.bchealth.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.FetchAuthenticatedRecordsRepository
import ca.bc.gov.repository.bcsc.BcscAuthRepo
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
    private val fetchAuthenticatedRecordsRepository: FetchAuthenticatedRecordsRepository
) : ViewModel() {

    private val _authStatus = MutableStateFlow(AuthStatus())
    val authStatus: StateFlow<AuthStatus> = _authStatus.asStateFlow()

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
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    isLoggedIn = isLoggedSuccess
                )
            }
            if (isLoggedSuccess) {
                fetchAuthenticatedRecordsRepository.fetchAuthenticatedRecords()
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
                    authRequestIntent = endSessionIntent
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

    fun processLogoutResponse() {
        bcscAuthRepo.processLogoutResponse()
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
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    isLoggedIn = isLoggedSuccess,
                    userName = userName
                )
            }
            //for testing
            // if (isLoggedSuccess) {
            //     fetchAuthenticatedRecordsRepository.fetchAuthenticatedRecords()
            // }
        } catch (e: Exception) {
            _authStatus.update {
                it.copy(
                    showLoading = false,
                    isLoggedIn = false
                )
            }
        }
    }

    fun resetAuthStatus() {
        _authStatus.update {
            it.copy(
                showLoading = false,
                isLoggedIn = false,
                authRequestIntent = null,
                isError = false,
                userName = ""
            )
        }
    }
}

data class AuthStatus(
    val showLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val authRequestIntent: Intent? = null,
    val isError: Boolean = false,
    val userName: String = ""
)
