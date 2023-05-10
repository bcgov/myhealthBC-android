package ca.bc.gov.bchealth.ui.home

import android.app.Activity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentHomeBinding
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BcscAuthState
import ca.bc.gov.bchealth.ui.auth.BiometricState
import ca.bc.gov.bchealth.ui.auth.BiometricsAuthenticationFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.fromHtml
import ca.bc.gov.bchealth.utils.hide
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.observeCurrentBackStackForAction
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseSecureFragment(R.layout.fragment_home) {

    private val binding by viewBindings(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var homeAdapter: HomeAdapter
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private var logoutResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            bcscAuthViewModel.processLogoutResponse(requireContext()).invokeOnCompletion {
                initRecyclerview()
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

        observeCurrentBackStackForAction<BiometricState>(BiometricsAuthenticationFragment.BIOMETRIC_STATE) {
            when (it) {
                BiometricState.SUCCESS -> onBiometricAuthSuccess()
                else -> findNavController().popBackStack()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    onBoardingFlow()
                }
            }
        }

        initRecyclerview()
        observeAuthStatus()

        bcscAuthViewModel.checkSession()

        viewModel.launchCheck()
        viewModel.getAuthenticatedPatientName()
        viewModel.bannerState.observe(viewLifecycleOwner) { displayBanner(it) }
    }

    private fun onBiometricAuthSuccess() {
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<BiometricState>(
            BiometricsAuthenticationFragment.BIOMETRIC_STATE
        )
        sharedViewModel.displayNotificationPermission = true
        viewModel.onAuthenticationRequired(false)
        viewModel.launchCheck()
        viewModel.executeOneTimeDataFetch()
    }

    override fun handleBCSCAuthState(bcscAuthState: BcscAuthState) {
        when (bcscAuthState) {
            BcscAuthState.SUCCESS -> {
                if (sharedViewModel.destinationId > 0) {
                    findNavController().navigate(sharedViewModel.destinationId)
                }
            }

            else -> {
                // no implementation required
            }
        }
    }

    private fun observeAuthStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bcscAuthViewModel.authStatus.collect {

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

    private fun initRecyclerview() {
        launchOnStart {
            homeAdapter = HomeAdapter { navigateToDestination(it) }
            binding.rvHome.adapter = homeAdapter
            viewModel.homeList.observe(viewLifecycleOwner) {
                homeAdapter.setData(it)
                homeAdapter.notifyDataSetChanged()
            }
            viewModel.getHomeRecordsList()
        }
    }

    private fun navigateToDestination(destinationType: HomeNavigationType) {
        val destination = when (destinationType) {
            HomeNavigationType.HEALTH_RECORD -> {
                if (bcscAuthViewModel.authStatus.value.loginStatus == LoginStatus.ACTIVE) {
                    R.id.action_homeFragment_to_health_records
                } else {
                    sharedViewModel.destinationId = R.id.health_records
                    R.id.bcscAuthInfoFragment
                }
            }

            HomeNavigationType.VACCINE_PROOF -> R.id.action_homeFragment_to_health_pass

            HomeNavigationType.RESOURCES -> R.id.action_homeFragment_to_resources

            HomeNavigationType.RECOMMENDATIONS -> R.id.action_homeFragment_to_recommendations
        }
        findNavController().navigate(destination)
    }

    private suspend fun onBoardingFlow() {
        viewModel.uiState.collect { uiState ->
            if (uiState.isOnBoardingRequired || uiState.isReOnBoardingRequired) {

                findNavController().navigate(
                    R.id.onBoardingSliderFragment,
                    bundleOf("reOnBoardingRequired" to uiState.isReOnBoardingRequired)
                )
                viewModel.onBoardingShown()
            }

            if (uiState.isBiometricAuthRequired && !sharedViewModel.isBiometricAuthShown) {
                findNavController().navigate(R.id.biometricsAuthenticationFragment)
                sharedViewModel.isBiometricAuthShown = true
                viewModel.onAuthenticationRequired(false)
            }

            if (sharedViewModel.displayNotificationPermission) {
                findNavController().navigate(R.id.notificationPermissionFragment)
            } else if (uiState.isBcscLoginRequiredPostBiometrics) {
                sharedViewModel.destinationId = 0
                findNavController().navigate(R.id.bcscAuthInfoFragment)
                sharedViewModel.isBCSCAuthShown = true
                viewModel.onBcscLoginRequired(false)
            }

            if (!uiState.patientFirstName.isNullOrBlank()) {
                binding.tvName.text = getString(R.string.hi)
                    .plus(" ")
                    .plus(uiState.patientFirstName)
                    .plus(",")
            } else {
                binding.tvName.text = getString(R.string.hello).plus(",")
            }

            if (uiState.isForceLogout) {
                bcscAuthViewModel.getEndSessionIntent()
                viewModel.onForceLogout(false)
            }

            if (uiState.displayServiceDownMessage) {
                binding.root.showServiceDownMessage(requireContext())
                viewModel.resetUiState()
            }
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.appbar) {
            stateListAnimator = null
            elevation = 0f
        }
        with(binding.layoutToolbar.topAppBar) {
            inflateMenu(R.menu.settings_menu)
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menu_settings -> {
                        findNavController().navigate(R.id.settingsFragment)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun displayBanner(banner: BannerItem) = with(binding.includeBanner) {
        if (banner.isHidden) {
            viewBanner.hide()
            return
        }

        tvTitle.text = banner.title

        tvBody.movementMethod = LinkMovementMethod.getInstance()
        tvBody.text = banner.body.fromHtml().trimEnd()

        ivToggle.isSelected = banner.expanded
        groupFullContent.toggleVisibility(banner.expanded)

        ivToggle.setOnClickListener {
            viewModel.toggleBanner()
        }

        tvLearnMore.toggleVisibility(banner.displayReadMore && banner.expanded)
        tvLearnMore.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToBannerDetail(
                title = banner.title,
                date = banner.date,
                body = banner.body,
            )
            findNavController().navigate(action)
        }
        tvDismiss.setOnClickListener {
            viewModel.dismissBanner()
        }

        viewBanner.show()
    }
}
