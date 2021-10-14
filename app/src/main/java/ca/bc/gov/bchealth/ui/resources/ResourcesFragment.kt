package ca.bc.gov.bchealth.ui.resources

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentResourcesBinding
import ca.bc.gov.bchealth.utils.toast
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResourcesFragment : Fragment(R.layout.fragment_resources) {

    private val binding by viewBindings(FragmentResourcesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: 14/10/21 Redirection URLs needs to be updated once available
        binding.customView1.setOnClickListener {
            redirect(getString(R.string.url_how_to_get_covid_vaccinated))
        }
        binding.customView2.setOnClickListener {
            redirect(getString(R.string.url_how_to_get_covid_vaccinated))
        }
        binding.customView3.setOnClickListener {
            redirect(getString(R.string.url_how_to_get_covid_vaccinated))
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
