package ca.bc.gov.bchealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.RecentPhnDobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
@HiltViewModel
class RecentPhnDobViewModel @Inject constructor(
    private val recentPhnDobRepository: RecentPhnDobRepository
) : ViewModel() {

    val recentPhnDob = recentPhnDobRepository.recentPhnDob

    fun setRecentPhnDobData(phn: String, dob: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            recentPhnDobRepository.setRecentPhnDob(phn, dob)
        }
    }
}