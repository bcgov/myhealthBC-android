package ca.bc.gov.bchealth.ui.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentOnboardingBinding
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import ca.bc.gov.bchealth.BuildConfig


/**
 * [OnBoardingFragment]
 *
 * @author amit metri
 */
@AndroidEntryPoint
class OnBoardingFragment : Fragment(R.layout.fragment_onboarding) {

    private lateinit var requestPermission: ActivityResultLauncher<String>

    private val binding by viewBindings(FragmentOnboardingBinding::bind)

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

        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA)){
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
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.camera_permission_required_title))
            .setCancelable(false)
            .setMessage(getString(R.string.camera_permission_message))
            .setNegativeButton(getString(R.string.not_now)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.grant)) { dialog, _ ->
                dialog.dismiss()
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri =
                        Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
            .show()
    }

    private fun navigateToScanner (){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.onBoardingFragment, true)
            .build()
        findNavController().navigate(R.id.barcodeScannerFragment, null, navOptions)
    }
}
