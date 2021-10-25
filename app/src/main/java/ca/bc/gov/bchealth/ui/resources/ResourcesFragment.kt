package ca.bc.gov.bchealth.ui.resources

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentResourcesBinding
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResourcesFragment : Fragment(R.layout.fragment_resources) {

    private val binding by viewBindings(FragmentResourcesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.customView1.setOnClickListener {
            requireActivity().redirect(getString(R.string.url_how_to_get_covid_vaccinated))
        }
        binding.customView2.setOnClickListener {
            requireActivity().redirect(getString(R.string.url_get_tested_for_covid))
        }
        binding.customView3.setOnClickListener {
            requireActivity().redirect(getString(R.string.url_covid_symptom_checker))
        }
        binding.customView4.setOnClickListener {
            requireActivity().redirect(getString(R.string.url_K12_daily_check))
        }
    }
}
