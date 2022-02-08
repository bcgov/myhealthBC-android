package ca.bc.gov.bchealth.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentBiometricAuthenticationBinding
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class BiometricsAuthenticationFragment : Fragment(R.layout.fragment_biometric_authentication) {

    private val binding by viewBindings(FragmentBiometricAuthenticationBinding::bind)
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var startForResult: ActivityResultLauncher<Intent?>

    companion object {
        const val BIOMETRIC_STATE = "BIOMETRIC_STATE"
        private const val AUTHENTICATORS = BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finishAndRemoveTask()
        }
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executor = ContextCompat.getMainExecutor(requireContext())

        binding.btnAuth.setOnClickListener {
            canAuthenticateUsingBioMetric()
        }
    }

    private fun canAuthenticateUsingBioMetric() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(AUTHENTICATORS)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt = getBioMetricPrompt()
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.app_name))
                    .setSubtitle(getString(R.string.dialog_biometric_description))
                    .setAllowedAuthenticators(AUTHENTICATORS)
                    .build()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                showAuthenticationErrorDialog(getString(R.string.error_biometric_unknown))
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                showNoHardwareDialog()
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {}
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showUserNotEnrolledDialog()
            }
        }
    }

    private fun getBioMetricPrompt(): BiometricPrompt {
        return BiometricPrompt(
            requireActivity(),
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> {}
                        else -> {
                            val errorMessage = "$errorCode, $errString"
                            showAuthenticationErrorDialog(errorMessage)
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        BIOMETRIC_STATE,
                        BioMetricState.FAILED
                    )
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        BIOMETRIC_STATE,
                        BioMetricState.SUCCESS
                    )
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun showNoHardwareDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.error_biometric_authentication_title)
            .setMessage(R.string.error_biometric_no_hardware)
            .setCancelable(false)
            .setNegativeButton(R.string.dialog_button_ok) { dialog, _ ->
                requireActivity().finishAndRemoveTask()
                dialog.dismiss()
            }.show()
    }

    private fun showAuthenticationErrorDialog(errorMessage: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.error_biometric_authentication_title)
            .setMessage(errorMessage)
            .setCancelable(false)
            .setNegativeButton(R.string.dialog_button_ok) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showUserNotEnrolledDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.error_biometric_enrollment_title)
            .setMessage(R.string.error_biometric_enrollment_message)
            .setCancelable(false)
            .setNegativeButton(R.string.dialog_btn_not_now) { dialog, _ ->
                requireActivity().finishAndRemoveTask()
                dialog.dismiss()
            }
            .setPositiveButton(R.string.dialog_btn_settings) { dialog, _ ->

                var enrollIntent: Intent?
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                AUTHENTICATORS
                            )
                        }
                    }
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.R && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                        enrollIntent = Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                    }
                    else -> {
                        enrollIntent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                    }
                }
                startForResult.launch(enrollIntent)
                dialog.dismiss()
            }.show()
    }
}

enum class BioMetricState {
    FAILED,
    SUCCESS
}
