package ca.bc.gov.bchealth

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.worker.MobileConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author pinakin.kansara
 * Created 2023-12-13 at 10:36 a.m.
 */
@HiltViewModel
class HealthGateWayViewModel @Inject constructor(
    private val mobileConfigRepository: MobileConfigRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HealthGateWayUiState())
    val uiState: StateFlow<HealthGateWayUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "HealthGateWayViewModel"
    }
    init {
        syncConfig()
    }

    private fun syncConfig() = viewModelScope.launch {
        Log.d(TAG, "synConfig() started")
        try {
            mobileConfigRepository.syncConfig()
        } catch (e: Exception) {
            Log.d(TAG, "syncConfig() exception = ${e.printStackTrace()}")
        } finally {
            _uiState.update { it.copy(keepSplashScreen = false) }
            Log.d(TAG, "syncConfig() completed")
        }
    }
}

@Stable
data class HealthGateWayUiState(
    val keepSplashScreen: Boolean = true
)
