package ca.bc.gov.bchealth.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentSettingBinding
import ca.bc.gov.bchealth.utils.toast
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

        binding.toolbar.apply {
            ivBack.visibility = View.VISIBLE
            ivBack.setImageResource(R.drawable.ic_acion_back)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.settings)
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        binding.viewPrivacyStatement.setOnClickListener {
            redirect(getString(R.string.url_privacy_policy))
        }
    }

    private fun redirect(url: String) {
        try {
            val customTabColorSchemeParams: CustomTabColorSchemeParams =
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(resources.getColor(R.color.white, null))
                    .setSecondaryToolbarColor(resources.getColor(R.color.white, null))
                    .build()

            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            val customTabIntent: CustomTabsIntent = builder
                .setDefaultColorSchemeParams(customTabColorSchemeParams)
                .setCloseButtonIcon(
                    resources.getDrawable(R.drawable.ic_acion_back, null)
                        .toBitmap()
                )
                .build()

            customTabIntent.launchUrl(
                requireContext(),
                Uri.parse(url)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            showURLFallBack(url)
        }
    }

    private fun showURLFallBack(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            context?.toast(getString(R.string.no_app_found))
        }
    }
}
