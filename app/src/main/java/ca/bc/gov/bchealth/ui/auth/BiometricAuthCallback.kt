package ca.bc.gov.bchealth.ui.auth

import androidx.biometric.BiometricPrompt

class BiometricAuthCallback(
    private val onSuccess: () -> Unit,
    private val onError: (errorCode: Int, errString: CharSequence) -> Unit,
) : BiometricPrompt.AuthenticationCallback() {

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        onSuccess()
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
            onError(errorCode, errString)
        }
    }
}
