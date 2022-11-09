package ca.bc.gov.bchealth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MAX_SPLASH_DELAY = 2000L

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val mobileConfigRepository: MobileConfigRepository,
) : ViewModel() {

    private val _updateType: MutableLiveData<UpdateType> = MutableLiveData()
    val updateType: LiveData<UpdateType>
        get() = _updateType

    fun checkAppVersion() {
        viewModelScope.launch {
            try {
                val remoteVersion = mobileConfigRepository.getRemoteApiVersion()

                val updateType = if (BuildConfig.HARDCODED_API_VERSION < remoteVersion) {
                    UpdateType.FORCE_UPDATE
                } else {
                    UpdateType.CHECK_SOFT_UPDATE
                }

                async { delay(MAX_SPLASH_DELAY) }.await()

                _updateType.value = updateType
            } catch (e: Exception) {
                e.printStackTrace()
                _updateType.value = UpdateType.CHECK_SOFT_UPDATE
            }
        }
    }

    enum class UpdateType {
        FORCE_UPDATE, CHECK_SOFT_UPDATE
    }
}
