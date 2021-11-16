package ca.bc.gov.bchealth.ui.setting

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentSettingBinding
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint

/**
 * [SettingFragment]
 *
 * @author amit metri
 */
@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {

    private val binding by viewBindings(FragmentSettingBinding::bind)

    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.settings)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        binding.txtPrivacyStatement.setOnClickListener {
            requireActivity().redirect(getString(R.string.url_privacy_policy))
        }

        binding.switchAnalytics.isChecked = Snowplow.getDefaultTracker()?.isTracking == true

        binding.switchAnalytics.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    Snowplow.getDefaultTracker()?.resume()
                    viewModel.trackAnalytics(true)
                }
                false -> {
                    Snowplow.getDefaultTracker()?.pause()
                    viewModel.trackAnalytics(false)
                }
            }
        }
    }
}
