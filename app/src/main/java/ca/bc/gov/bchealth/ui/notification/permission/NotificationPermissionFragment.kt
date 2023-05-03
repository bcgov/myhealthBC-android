package ca.bc.gov.bchealth.ui.notification.permission

import androidx.compose.runtime.Composable
import ca.bc.gov.bchealth.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationPermissionFragment : BaseFragment(null) {

    @Composable
    override fun GetComposableLayout() {
        NotificationPermissionUI(
            acceptAction = ::requestPermission,
            cancelAction = ::navigateToNextScreen,
        )
    }

    private fun requestPermission() {
    }

    private fun navigateToNextScreen() {
    }
}
