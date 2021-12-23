package ca.bc.gov.bchealth.ui.login

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.repository.ErrorData
import ca.bc.gov.bchealth.repository.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val uiStateMutableSharedFlow = MutableSharedFlow<Response<String>>()
    val uiStateSharedFlow: SharedFlow<Response<String>>
        get() = uiStateMutableSharedFlow.asSharedFlow()

    fun initiateLogin(authResultLauncher: ActivityResultLauncher<Intent>, requireContext: Context) =
        viewModelScope.launch {

            uiStateMutableSharedFlow.emit(Response.Loading())

            try {
                authManagerRepo.initializeLogin(authResultLauncher, requireContext)
            } catch (e: Exception) {
                println(e.message)
                uiStateMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
            }
        }

    fun processAuthResponse(activityResult: ActivityResult) = viewModelScope.launch {
        try {
            authManagerRepo.processAuthResponse(activityResult)
            uiStateMutableSharedFlow.emit(Response.Success())
        } catch (e: Exception) {
            println(e.message)
            uiStateMutableSharedFlow.emit(Response.Error(ErrorData.GENERIC_ERROR))
        }
    }
}
