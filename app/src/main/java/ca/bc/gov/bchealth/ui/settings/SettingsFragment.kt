package ca.bc.gov.bchealth.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentOnboardingBinding
import ca.bc.gov.bchealth.databinding.FragmentSettingsBinding
import ca.bc.gov.bchealth.ui.onboarding.OnBoardingFragment
import ca.bc.gov.bchealth.utils.viewBindings

/**
 * [SettingsFragment]
 *
 * @author amit metri
 */
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBindings(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtPrivacyStatement.setOnClickListener {
            // TODO: 21/09/21 being tracked on https://freshworks.atlassian.net/browse/VCBA-49
        }

        binding.txtHelp.setOnClickListener {
            // TODO: 21/09/21 being tracked on https://freshworks.atlassian.net/browse/VCBA-49
        }

    }

}