package ca.bc.gov.bchealth.ui.setting

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentSettingsBinding
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BcscAuthState
import ca.bc.gov.bchealth.ui.NavigationAction
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.setActionToPreviousBackStackEntry
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import ca.bc.gov.common.BuildConfig.FLAG_VIEW_PROFILE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
* @auther amit_metri on 07,January,2022
*/
@AndroidEntryPoint
class SettingsFragment : BaseSecureFragment(R.layout.fragment_settings) {

    private val binding by viewBindings(FragmentSettingsBinding::bind)
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var logoutResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            bcscAuthViewModel.processLogoutResponse(requireContext())
            if (findNavController().previousBackStackEntry?.destination?.id ==
                R.id.healthRecordFragment
            ) {
                findNavController().setActionToPreviousBackStackEntry(
                    NAVIGATION_ACTION,
                    NavigationAction.ACTION_RE_CHECK
                )
            }
        } else {
            AlertDialogHelper.showAlertDialog(
                context = requireContext(),
                title = getString(R.string.error),
                msg = getString(R.string.error_message),
                positiveBtnMsg = getString(R.string.dialog_button_ok)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLogin()

        binding.tvViewProfile.toggleVisibility(FLAG_VIEW_PROFILE)
        if (FLAG_VIEW_PROFILE) {
            binding.layoutProfile.setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
        }
    }

    override fun handleBCSCAuthState(bcscAuthState: BcscAuthState) {
        when (bcscAuthState) {
            BcscAuthState.SUCCESS -> {
                findNavController().setActionToPreviousBackStackEntry(
                    NAVIGATION_ACTION,
                    NavigationAction.ACTION_RE_CHECK
                )
            }

            BcscAuthState.NOT_NOW -> {}
            BcscAuthState.NO_ACTION -> {}
        }
    }

    private fun initClickListeners() {

        binding.apply {

            tvDataSecurity.setOnClickListener {
                findNavController().navigate(R.id.securityAndDataFragment)
            }

            tvFeedback.setOnClickListener {
                findNavController().navigate(R.id.feedbackFragment)
            }

            tvPrivacyStatement.setOnClickListener {
                requireActivity().redirect(getString(R.string.url_privacy_policy))
            }

            tvLogOut.setOnClickListener {
                showLogoutDialog()
            }

            btnLogin.setOnClickListener {
                sharedViewModel.destinationId = 0
                findNavController().navigate(R.id.bcscAuthInfoFragment)
            }
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setupWithNavController(findNavController(), appBarConfiguration)
            setNavigationIcon(R.drawable.ic_toolbar_back)
            title = getString(R.string.profile_settings)
        }
    }

    private fun checkLogin() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bcscAuthViewModel.authStatus.collect {

                    binding.progressBar.isVisible = it.showLoading
                    binding.tvPrivacyStatement.isClickable = !it.showLoading
                    binding.tvDataSecurity.isClickable = !it.showLoading
                    binding.tvFullName.text = it.userName

                    if (it.showLoading) {
                        return@collect
                    } else {
                        initClickListeners()
                        val isLoginStatusActive = it.loginStatus == LoginStatus.ACTIVE

                        binding.apply {
                            layoutProfile.isVisible = isLoginStatusActive
                            layoutLogin.isVisible = !isLoginStatusActive
                            tvFeedback.isVisible = isLoginStatusActive
                            line4.isVisible = isLoginStatusActive
                            tvLogOut.isVisible = isLoginStatusActive
                            line5.isVisible = isLoginStatusActive
                        }
                        if (it.isError) {
                            bcscAuthViewModel.resetAuthStatus()
                        }

                        if (it.endSessionIntent != null) {
                            logoutResultLauncher.launch(it.endSessionIntent)
                            bcscAuthViewModel.resetAuthStatus()
                        }
                    }
                }
            }
        }

        bcscAuthViewModel.checkSession()
    }

    private fun showLogoutDialog() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.logout_dialog_title),
            msg = getString(R.string.logout_dialog_message),
            positiveBtnMsg = getString(R.string.log_out),
            negativeBtnMsg = getString(R.string.cancel),
            positiveBtnCallback = {
                bcscAuthViewModel.getEndSessionIntent()
            }
        )
    }
}
