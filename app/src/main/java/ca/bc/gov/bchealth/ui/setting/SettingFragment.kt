package ca.bc.gov.bchealth.ui.setting

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentSettingBinding
import ca.bc.gov.bchealth.utils.showAlertDialog
import ca.bc.gov.bchealth.utils.showError
import ca.bc.gov.bchealth.utils.viewBindings
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * [SettingFragment]
 *
 * @author amit metri
 */
@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {

    private val binding by viewBindings(FragmentSettingBinding::bind)

    private val viewModel: SettingsViewModel by viewModels()

    private var isLoggedIn: Boolean = false

    private var logoutResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            viewModel.processLogoutResponse()
            isLoggedIn = false
            binding.switchLogin.isChecked = false
        } else {
            requireContext().showError(
                getString(R.string.error),
                getString(R.string.error_message)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar()

        initUi()
    }

    private fun setUpToolbar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.settings)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initUi() {

        showLoader(true)

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isLoggedIn.collect {
                    it?.let {
                        showLoader(false)
                        binding.switchLogin.isChecked = it
                        isLoggedIn = it
                        bcscLoginSwitch()
                    }
                }
            }
        }

        viewModel.checkProfile()

        analyticsSwitch()

        binding.tvDeleteAllRecords.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun showLoader(value: Boolean) {
        binding.progressBar.isVisible = value
    }

    private fun analyticsSwitch() {
        binding.switchAnalytics.isChecked = Snowplow.getDefaultTracker()?.isTracking == false

        binding.switchAnalytics.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    Snowplow.getDefaultTracker()?.pause()
                    viewModel.trackAnalytics(false)
                }
                false -> {
                    Snowplow.getDefaultTracker()?.resume()
                    viewModel.trackAnalytics(true)
                }
            }
        }
    }

    private fun bcscLoginSwitch() {

        binding.switchLogin.setOnClickListener {

            binding.switchLogin.isChecked = isLoggedIn

            if (isLoggedIn) {
                showLogoutDialog()
            } else {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.settingFragment, true)
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                    .build()

                viewModel.checkLogin(
                    R.id.settingFragment,
                    navOptions,
                    findNavController()
                )
            }
        }
    }

    private fun showLogoutDialog() {
        requireContext().showAlertDialog(
            title = getString(R.string.logout_dialog_title),
            message = getString(R.string.logout_dialog_message),
            positiveButtonText = getString(R.string.log_out),
            negativeButtonText = getString(R.string.cancel)
        ) {
            viewModel.initLogout(logoutResultLauncher)
        }
    }

    private fun showAlertDialog() {

        requireContext().showAlertDialog(
            title = getString(R.string.delete_data),
            message = getString(R.string.delete_data_message),
            positiveButtonText = getString(R.string.delete),
            negativeButtonText = getString(R.string.cancel)
        ) {
            deleteAllRecordsAndSavedData()
        }
    }

    private fun deleteAllRecordsAndSavedData() {
        viewModel.deleteAllRecordsAndSavedData().invokeOnCompletion {
            navigatePostDeletion()
        }
    }

    private fun navigatePostDeletion() {
        findNavController().popBackStack(R.id.myCardsFragment, false)
    }
}
