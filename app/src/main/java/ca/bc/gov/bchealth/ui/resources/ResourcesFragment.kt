package ca.bc.gov.bchealth.ui.resources

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentResourcesBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.common.model.analytics.AnalyticsAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResourcesFragment : BaseFragment(R.layout.fragment_resources) {

    private val binding by viewBindings(FragmentResourcesBinding::bind)
    private val resourcesViewModel: ResourcesViewModel by viewModels()
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()
    private lateinit var resourcesAdapter: ResourcesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        resourcesAdapter = ResourcesAdapter { url ->
            requireActivity().redirect(url)
            seedAnalyticsData(url = url)
        }
        binding.rvResources.adapter = resourcesAdapter
        binding.rvResources.layoutManager = LinearLayoutManager(requireContext())
        resourcesAdapter.submitList(resourcesViewModel.prepareResourcesList())
    }

    private fun seedAnalyticsData(url: String) {
        // Snowplow event
        analyticsFeatureViewModel.track(AnalyticsAction.RESOURCE_CLICK, url)
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            title = getString(R.string.health_resources)
        }
    }
}
