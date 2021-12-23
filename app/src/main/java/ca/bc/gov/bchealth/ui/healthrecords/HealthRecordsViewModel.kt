package ca.bc.gov.bchealth.ui.healthrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import ca.bc.gov.bchealth.repository.HealthRecordsRepository
import ca.bc.gov.bchealth.ui.login.AuthManagerRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by amit_metri on 23,November,2021
*/
@HiltViewModel
class HealthRecordsViewModel @Inject constructor(
    val healthRecordsRepository: HealthRecordsRepository,
    val authManagerRepo: AuthManagerRepo
) : ViewModel() {

    val healthRecords = healthRecordsRepository.healthRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun checkLogin(
        destinationId: Int,
        navOptions: NavOptions,
        navController: NavController
    ) = viewModelScope.launch {
        authManagerRepo.checkLogin(destinationId, navOptions, navController)
    }
}
