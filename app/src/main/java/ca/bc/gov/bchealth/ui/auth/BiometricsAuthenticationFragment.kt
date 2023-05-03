package ca.bc.gov.bchealth.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.addCallback
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
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.setActionToPreviousBackStackEntry
import ca.bc.gov.bchealth.utils.viewBindings
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
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // no implementation required
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executor = ContextCompat.getMainExecutor(requireContext())

        binding.btnAuth.setOnClickListener {
            canAuthenticateUsingBioMetric()
        }

        binding.btnLearnMore.setOnClickListener {
            findNavController().navigate(
                R.id.action_biometricsAuthenticationFragment_to_biometricSecurityTipFragment
            )
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

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                // no implementation required
            }

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
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            // no implementation required
                        }

                        else -> {
                            val errorMessage = "$errorCode, $errString"
                            showAuthenticationErrorDialog(errorMessage)
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    findNavController().setActionToPreviousBackStackEntry(
                        BIOMETRIC_STATE,
                        BiometricState.FAILED
                    )
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    findNavController().setActionToPreviousBackStackEntry(
                        BIOMETRIC_STATE,
                        BiometricState.SUCCESS
                    )
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun showNoHardwareDialog() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.error_biometric_authentication_title),
            msg = getString(R.string.error_biometric_no_hardware),
            positiveBtnMsg = getString(R.string.dialog_button_ok),
            positiveBtnCallback = {
                requireActivity().finishAndRemoveTask()
            },
        )
    }

    private fun showAuthenticationErrorDialog(errorMessage: String) {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.error_biometric_authentication_title),
            msg = errorMessage,
            positiveBtnMsg = getString(R.string.dialog_button_ok)
        )
    }

    private fun showUserNotEnrolledDialog() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.error_biometric_enrollment_title),
            msg = getString(R.string.error_biometric_enrollment_message),
            positiveBtnMsg = getString(R.string.dialog_btn_settings),
            negativeBtnMsg = getString(R.string.dialog_btn_not_now),
            positiveBtnCallback = {
                val enrollIntent: Intent?
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
            },
            negativeBtnCallback = {
                requireActivity().finishAndRemoveTask()
            }
        )
    }
}

enum class BiometricState {
    FAILED,
    SUCCESS
}
