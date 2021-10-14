package ca.bc.gov.bchealth.ui.setting

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
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

    companion object {
        private const val CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome"
    }

    private val binding by viewBindings(FragmentSettingBinding::bind)

    private lateinit var customTabsSession: CustomTabsSession

    override fun onAttach(context: Context) {
        super.onAttach(context)

        /*
        * For quick loading of the URL
        * */
        val connection: CustomTabsServiceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                client.warmup(0L)
                customTabsSession = client.newSession(null)!!
                try {
                    customTabsSession
                        .mayLaunchUrl(
                            Uri.parse(getString(R.string.url_privacy_policy)),
                            null,
                            null
                        )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        }
        CustomTabsClient.bindCustomTabsService(
            requireContext(),
            CUSTOM_TAB_PACKAGE_NAME,
            connection
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPrivacyStatement.setOnClickListener {

            try {
                val customTabColorSchemeParams: CustomTabColorSchemeParams =
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(resources.getColor(R.color.white, null))
                        .setSecondaryToolbarColor(resources.getColor(R.color.white, null))
                        .build()

                val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder(customTabsSession)
                val customTabIntent: CustomTabsIntent = builder
                    .setDefaultColorSchemeParams(customTabColorSchemeParams)
                    .setCloseButtonIcon(resources.getDrawable(R.drawable.ic_acion_back, null)
                        .toBitmap())
                    .build()

                customTabIntent.launchUrl(
                    requireContext(),
                    Uri.parse(getString(R.string.url_privacy_policy))
                )
            } catch (e: Exception) {
                e.printStackTrace()
                showURLFallBack()
            }
        }
    }

    private fun showURLFallBack() {
        val webpage: Uri = Uri.parse(getString(R.string.url_privacy_policy))
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            context?.toast(getString(R.string.no_app_found))
        }
    }
}
