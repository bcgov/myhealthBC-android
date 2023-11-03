package ca.bc.gov.bchealth.ui.resources

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.common.model.analytics.AnalyticsAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResourcesFragment : BaseFragment(null) {

    private val resourcesViewModel: ResourcesViewModel by viewModels()
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        ResourcesUI(
            uiList = resourcesViewModel.getResourcesList(),
            navigationAction = { findNavController().popBackStack() },
            onClickResource = ::onClickResource
        )
    }

    private fun onClickResource(urlRes: Int) {
        val url = getString(urlRes)
        requireActivity().redirect(url)
        seedAnalyticsData(url = url)
    }

    private fun seedAnalyticsData(url: String) {
        analyticsFeatureViewModel.track(AnalyticsAction.RESOURCE_CLICK, url)
    }
}
