package ca.bc.gov.bchealth.ui.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentSettingBinding
import ca.bc.gov.bchealth.utils.viewBindings

/**
 * [SettingFragment]
 *
 * @author amit metri
 */
class SettingFragment : Fragment(R.layout.fragment_setting) {

    private val binding by viewBindings(FragmentSettingBinding::bind)

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
