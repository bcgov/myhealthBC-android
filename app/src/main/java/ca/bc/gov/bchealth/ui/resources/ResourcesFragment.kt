package ca.bc.gov.bchealth.ui.resources

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.analytics.AnalyticsAction
import ca.bc.gov.bchealth.analytics.SelfDescribingEvent
import ca.bc.gov.bchealth.databinding.FragmentResourcesBinding
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResourcesFragment : Fragment(R.layout.fragment_resources) {

    private val binding by viewBindings(FragmentResourcesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        binding.customView4.setOnClickListener {
            val url = getString(R.string.url_K12_daily_check)
            requireActivity().redirect(url)
            seedAnalyticsData(url = url)
        }
    }

    private fun seedAnalyticsData(url: String) {
        // Snowplow event
        Snowplow.getDefaultTracker()?.track(
            SelfDescribingEvent.get(AnalyticsAction.ResourceLinkSelected.value, url)
        )
    }
}
