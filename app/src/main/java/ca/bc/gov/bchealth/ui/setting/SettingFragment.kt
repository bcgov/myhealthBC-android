package ca.bc.gov.bchealth.ui.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentSettingBinding
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.common.model.settings.AnalyticsFeature
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()
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

        binding.switchAnalytics.isChecked = Snowplow.getDefaultTracker()?.isTracking == false

        binding.switchAnalytics.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    Snowplow.getDefaultTracker()?.pause()
                    analyticsFeatureViewModel.toggleAnalyticsFeature(AnalyticsFeature.DISABLED)
                }
                false -> {
                    Snowplow.getDefaultTracker()?.resume()
                    analyticsFeatureViewModel.toggleAnalyticsFeature(AnalyticsFeature.ENABLED)
                }
            }
        }

        binding.tvDeleteAllRecords.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_data))
            .setCancelable(false)
            .setMessage(getString(R.string.delete_data_message))
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                deleteAllRecordsAndSavedData()
                dialog.dismiss()
            }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteAllRecordsAndSavedData() {
        viewModel.deleteAllRecordsAndSavedData().invokeOnCompletion {
            navigatePostDeletion()
        }
    }

    private fun navigatePostDeletion() {
        findNavController().popBackStack(R.id.healthPassFragment, false)
    }
}
