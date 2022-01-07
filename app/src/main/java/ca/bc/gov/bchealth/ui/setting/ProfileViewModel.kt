package ca.bc.gov.bchealth.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.repository.AuthManagerRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 07,January,2022
*/
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authManagerRepo: AuthManagerRepo
) : ViewModel() {

    val isLoggedIn = authManagerRepo.loginSharedFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    fun checkProfile() = viewModelScope.launch {
        authManagerRepo.checkProfile()
    }
}
