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
import ca.bc.gov.bchealth.databinding.FragmentProfileBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordPlaceholderFragment
import ca.bc.gov.bchealth.ui.healthrecord.NavigationAction
import ca.bc.gov.bchealth.ui.healthrecord.filter.FilterViewModel
import ca.bc.gov.bchealth.ui.healthrecord.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthState
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
* @auther amit_metri on 07,January,2022
*/
@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val binding by viewBindings(FragmentProfileBinding::bind)
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val filterSharedViewModel: FilterViewModel by activityViewModels()

    private var logoutResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            bcscAuthViewModel.processLogoutResponse(requireContext())
            if (findNavController().previousBackStackEntry?.destination?.id ==
                R.id.individualHealthRecordFragment
            ) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
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

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BcscAuthState>(
            BcscAuthFragment.BCSC_AUTH_STATUS
        )?.observe(viewLifecycleOwner) {
            if (it == BcscAuthState.SUCCESS) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
                    NavigationAction.ACTION_RE_CHECK
                )
            }
        }
    }

    private fun initClickListeners() {

        binding.apply {

            tvDataSecurity.setOnClickListener {
                findNavController().navigate(R.id.settingFragment)
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
                        binding.layoutProfile.isVisible = isLoginStatusActive
                        binding.layoutLogin.isVisible = !isLoginStatusActive
                        binding.tvLogOut.isVisible = isLoginStatusActive

                        if (it.isError) {
                            bcscAuthViewModel.resetAuthStatus()
                        }

                        if (it.endSessionIntent != null) {
                            logoutResultLauncher.launch(it.endSessionIntent)
                            bcscAuthViewModel.resetAuthStatus()
                            filterSharedViewModel.updateFilter(mutableListOf(TimelineTypeFilter.ALL.name), null, null)
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
