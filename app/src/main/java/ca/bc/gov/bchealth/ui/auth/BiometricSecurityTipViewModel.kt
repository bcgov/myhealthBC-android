package ca.bc.gov.bchealth.ui.auth

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BiometricSecurityTipViewModel @Inject constructor() : ViewModel() {

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

data class SecurityTipItem(
    @DrawableRes val icon: Int,
    @StringRes val title: Int
)
