package ca.bc.gov.bchealth.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import ca.bc.gov.bchealth.datasource.DataStoreRepo
import ca.bc.gov.bchealth.ui.login.AuthManagerRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 02,November,2021
*/
@HiltViewModel
class NewFeatureViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepo,
    private val authManagerRepo: AuthManagerRepo
) : ViewModel() {

    fun setNewFeatureShown(shown: Boolean) = viewModelScope.launch {
        dataStoreRepo.setNewFeatureShown(shown)
    }

    fun checkLogin(
        destinationId: Int,
        navOptions: NavOptions,
        navController: NavController
    ) = viewModelScope.launch {
        authManagerRepo.checkLogin(destinationId, navOptions, navController)
    }
}
