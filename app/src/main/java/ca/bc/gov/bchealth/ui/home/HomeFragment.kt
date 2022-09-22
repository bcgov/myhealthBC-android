package ca.bc.gov.bchealth.ui.home

import android.app.Activity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentHomeBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.auth.BioMetricState
import ca.bc.gov.bchealth.ui.auth.BiometricsAuthenticationFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthState
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.fromHtml
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

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

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BioMetricState>(
            BiometricsAuthenticationFragment.BIOMETRIC_STATE
        )?.observe(viewLifecycleOwner) {
            when (it) {
                BioMetricState.SUCCESS -> {
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<BioMetricState>(
                        BiometricsAuthenticationFragment.BIOMETRIC_STATE
                    )
                    viewModel.onAuthenticationRequired(false)
                    viewModel.launchCheck()
                    viewModel.executeOneTimeDataFetch()
                }
                else -> {
                    findNavController().popBackStack()
                }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BcscAuthState>(
            BcscAuthFragment.BCSC_AUTH_STATUS
        )?.observe(viewLifecycleOwner) {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<BcscAuthState>(
                BcscAuthFragment.BCSC_AUTH_STATUS
            )
            when (it) {
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeAdapter = HomeAdapter {
                    when (it) {
                        HomeNavigationType.HEALTH_RECORD -> {
                            if (bcscAuthViewModel.authStatus.value.loginStatus != null && bcscAuthViewModel.authStatus.value.loginStatus == LoginStatus.ACTIVE) {
                                findNavController().navigate(R.id.action_homeFragment_to_health_records)
                            } else {
                                sharedViewModel.destinationId = R.id.health_records
                                findNavController().navigate(R.id.bcscAuthInfoFragment)
                            }
                        }
                        HomeNavigationType.VACCINE_PROOF -> {
                            findNavController().navigate(R.id.action_homeFragment_to_health_pass)
                        }
                        HomeNavigationType.RESOURCES -> {
                            findNavController().navigate(R.id.action_homeFragment_to_resources)
                        }
                        HomeNavigationType.RECOMMENDATIONS -> {
                            findNavController().navigate(R.id.action_homeFragment_to_recommendations)
                        }
                    }
                }
                binding.rvHome.adapter = homeAdapter

                viewModel.homeList.observe(viewLifecycleOwner) {
                    homeAdapter.submitList(it)
                }
                viewModel.getHomeRecordsList()
            }
        }
    }

    private suspend fun onBoardingFlow() {
        viewModel.uiState.collect { uiState ->
            if (uiState.isOnBoardingRequired) {
                findNavController().navigate(R.id.onBoardingSliderFragment)
                viewModel.onBoardingShown()
            }

            if (uiState.isAuthenticationRequired && !sharedViewModel.isBiometricAuthShown) {
                findNavController().navigate(R.id.biometricsAuthenticationFragment)
                sharedViewModel.isBiometricAuthShown = true
                viewModel.onAuthenticationRequired(false)
            }

            if (uiState.isBcscLoginRequiredPostBiometrics) {
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
                        findNavController().navigate(R.id.profileFragment)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun displayBanner(banner: BannerItem) = with(binding.includeBanner) {
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
            println("dismiss")
        }

        viewBanner.show()
    }
}
