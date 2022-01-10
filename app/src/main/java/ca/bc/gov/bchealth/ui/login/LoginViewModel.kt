package ca.bc.gov.bchealth.ui.login

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import ca.bc.gov.bchealth.repository.AuthManagerRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 12,January,2022
*/
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authManagerRepo: AuthManagerRepo
) : ViewModel() {

    fun checkLogin(
        destinationId: Int,
        navOptions: NavOptions,
        navController: NavController
    ) = viewModelScope.launch {
        authManagerRepo.checkLogin(destinationId, navOptions, navController)
    }

    fun initLogout(logoutResultLauncher: ActivityResultLauncher<Intent>) = viewModelScope.launch {
        try {
            authManagerRepo.logout(logoutResultLauncher)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun processLogoutResponse() = viewModelScope.launch {
        try {
            authManagerRepo.processLogoutResponse()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    val isLoggedIn = authManagerRepo.loginSharedFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    fun checkProfile() = viewModelScope.launch {
        authManagerRepo.checkProfile()
    }
}
