package ca.bc.gov.bchealth.ui.notification.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationPermissionFragment : BaseFragment(null) {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var requestPermission: ActivityResultLauncher<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel.displayNotificationPermission = false
        requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    popNavigation()
                } else {
                    showRationalDialog()
                }
            }

        if (isNotificationPermissionGranted()) popNavigation()
    }

    private fun showRationalDialog() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.notification_permission_required_title),
            msg = getString(R.string.notification_permission_required_message),
            positiveBtnMsg = getString(R.string.grant),
            negativeBtnMsg = getString(R.string.not_now),
            positiveBtnCallback = ::openAppSettings
        )
    }

    @Composable
    override fun GetComposableLayout() {
        NotificationPermissionUI(
            acceptAction = ::requestPermission,
            cancelAction = ::popNavigation,
        )
    }

    private fun requestPermission() {
        when {
            isNotificationPermissionGranted() -> popNavigation()

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> showRationalDialog()

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> requestPermission.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )

            else -> openAppSettings()
        }
    }

    @SuppressLint("InlinedApi")
    private fun isNotificationPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    private fun openAppSettings() {
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
}
