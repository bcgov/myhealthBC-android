package ca.bc.gov.bchealth.ui.auth

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BiometricSecurityTipViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BiometricSecurityTipUIState())
    val uiState: StateFlow<BiometricSecurityTipUIState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(securityTips = getSecurityTipList()) }
    }

    fun getSecurityTipList(): List<SecurityTipItem> {

        return listOf(
            SecurityTipItem(
                R.drawable.ic_finger_print,
                R.string.biometric_security_tip_1
            ),
            SecurityTipItem(
                R.drawable.ic_passcode,
                R.string.biometric_security_tip_2
            ),
            SecurityTipItem(
                R.drawable.ic_unlocked_device,
                R.string.biometric_security_tip_3
            )
        )
    }
}

data class BiometricSecurityTipUIState(
    @StringRes val securityTipInfo: Int = R.string.biometric_security_tip_info,
    val securityTips: List<SecurityTipItem> = emptyList()
)
data class SecurityTipItem(
    @DrawableRes val icon: Int,
    @StringRes val title: Int
)
