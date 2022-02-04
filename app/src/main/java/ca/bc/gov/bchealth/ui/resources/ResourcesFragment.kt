package ca.bc.gov.bchealth.ui.resources

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentResourcesBinding
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.common.model.analytics.AnalyticsAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResourcesFragment : Fragment(R.layout.fragment_resources) {

    private val binding by viewBindings(FragmentResourcesBinding::bind)
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        binding.customView1.setOnClickListener {
            val url = getString(R.string.url_how_to_get_covid_vaccinated)
            requireActivity().redirect(url)
            seedAnalyticsData(url = url)
        }
        binding.customView2.setOnClickListener {
            val url = getString(R.string.url_get_tested_for_covid)
            requireActivity().redirect(url)
            seedAnalyticsData(url = url)
        }
        binding.customView3.setOnClickListener {
            val url = getString(R.string.url_covid_symptom_checker)
            requireActivity().redirect(url)
            seedAnalyticsData(url = url)
        }
    }

    private fun seedAnalyticsData(url: String) {
        // Snowplow event
        analyticsFeatureViewModel.track(AnalyticsAction.RESOURCE_CLICK, url)
    }

    private fun setupToolBar() {
        binding.toolbar.ivRightOption.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
        }
    }
}
