package ca.bc.gov.bchealth.ui.login

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.repository.AuthManagerRepo
import ca.bc.gov.bchealth.repository.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* @auther amit_metri on 05,January,2022
*/
@HiltViewModel
class LoginInfoViewModel @Inject constructor(
    private val authManagerRepo: AuthManagerRepo
) : ViewModel() {

    /*
    * Used to manage Success, Error and Loading status in the UI
    * */
    val uiStateSharedFlow: SharedFlow<Response<String>>
        get() = authManagerRepo.uiStateSharedFlow

    fun initiateLogin(authResultLauncher: ActivityResultLauncher<Intent>, requireContext: Context) =
        viewModelScope.launch {

            try {
                authManagerRepo.initializeLogin(authResultLauncher, requireContext)
            } catch (e: Exception) {
                println(e.message)
            }
        }

    fun processAuthResponse(activityResult: ActivityResult) = viewModelScope.launch {
        try {
            authManagerRepo.processAuthResponse(activityResult)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
