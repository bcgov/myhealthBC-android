package ca.bc.gov.bchealth.ui.onboarding

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentCameraPermissionBinding
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

/**
 * [CameraPermissionFragment]
 *
 * @author amit metri
 */
@AndroidEntryPoint
class CameraPermissionFragment : Fragment(R.layout.fragment_camera_permission) {

    private lateinit var requestPermission: ActivityResultLauncher<String>

    private val binding by viewBindings(FragmentCameraPermissionBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->

            if (isGranted) {
                navigateToScanner()
            } else {
                showRationalDialog()
            }
        }

        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            )
        ) {
            navigateToScanner()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAllowCameraPermission.setOnClickListener {
            checkCameraPermission()
        }

        binding.txtSkipForNow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    /**
     * Check if permission for required feature is Granted or not.
     */
    private fun checkCameraPermission() {

        when {

            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                navigateToScanner()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showRationalDialog()
            }

            else -> {
                requestPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showRationalDialog() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.camera_permission_required_title),
            msg = getString(R.string.camera_permission_message),
            positiveBtnMsg = getString(R.string.grant),
            negativeBtnMsg = getString(R.string.not_now),
            positiveBtnCallback = {
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri =
                        Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: Exception) {
                    // no implementation required
                }
            }
        )
    }

    private fun navigateToScanner() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.onBoardingFragment, true)
            .build()
        findNavController().navigate(R.id.barcodeScannerFragment, null, navOptions)
    }
}
