package ca.bc.gov.bchealth.ui.profile

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.URL_ADDRESS_CHANGE
import ca.bc.gov.bchealth.utils.URL_COMMUNICATION_PREFS
import ca.bc.gov.bchealth.utils.redirect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment(null) {

    val viewModel: ProfileViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        ProfileUI(
            viewModel = viewModel,
            navigationAction = ::popNavigation,
            onClickAddress = ::onClickAddressChange,
            onClickPrefs = ::onClickCommunicationPrefs,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.collectOnStart {
            if (it.error != null) popNavigation()
        }
        viewModel.load()
    }

    private fun onClickAddressChange() {
        requireContext().redirect(URL_ADDRESS_CHANGE)
    }

    private fun onClickCommunicationPrefs() {
        requireContext().redirect(URL_COMMUNICATION_PREFS)
    }
}
