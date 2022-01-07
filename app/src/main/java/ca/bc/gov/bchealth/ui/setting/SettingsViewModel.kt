package ca.bc.gov.bchealth.ui.setting

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import ca.bc.gov.bchealth.datasource.LocalDataSource
import ca.bc.gov.bchealth.repository.AuthManagerRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState
import javax.inject.Inject

/*
* Created by amit_metri on 16,November,2021
*/
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepo,
    private val localDataSource: LocalDataSource,
    private val authManagerRepo: AuthManagerRepo
) : ViewModel() {

    fun trackAnalytics(value: Boolean) = viewModelScope.launch {
        dataStoreRepo.trackAnalytics(value)
    }

    fun deleteAllRecordsAndSavedData() = viewModelScope.launch {
        localDataSource.deleteAllRecords()
        dataStoreRepo.setRecentFormData("")
        dataStoreRepo.setAuthState(AuthState())
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

    fun checkLogin(
        destinationId: Int,
        navOptions: NavOptions,
        navController: NavController
    ) = viewModelScope.launch {
        authManagerRepo.checkLogin(destinationId, navOptions, navController)
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
