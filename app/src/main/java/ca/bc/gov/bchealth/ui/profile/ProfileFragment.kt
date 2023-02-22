package ca.bc.gov.bchealth.ui.profile

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import ca.bc.gov.bchealth.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment(null) {

    val viewModel: ProfileViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        ProfileUI(viewModel = viewModel, navigationAction = ::popNavigation, ::onClickAddressChange)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.load()
    }

    private fun onClickAddressChange() {
    }
}
