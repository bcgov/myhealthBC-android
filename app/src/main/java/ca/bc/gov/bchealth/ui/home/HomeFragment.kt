package ca.bc.gov.bchealth.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BcscAuthState
import ca.bc.gov.bchealth.ui.auth.BioMetricState
import ca.bc.gov.bchealth.ui.auth.BiometricsAuthenticationFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthToolBar
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.observeCurrentBackStackForAction
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseSecureFragment(null) {

    private val viewModel: HomeViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val homeViewModel: HomeComposeViewModel by viewModels()

    private var logoutResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            bcscAuthViewModel.processLogoutResponse(requireContext()).invokeOnCompletion {
                updateHomeRecordsList()
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

        observeCurrentBackStackForAction<BioMetricState>(BiometricsAuthenticationFragment.BIOMETRIC_STATE) {
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    onBoardingFlow()
                }
            }
        }

        updateHomeRecordsList()
        observeAuthStatus()

        bcscAuthViewModel.checkSession()

        viewModel.launchCheck()
        viewModel.getAuthenticatedPatientName()
    }

    @Composable
    override fun GetComposableLayout() {
        val homeUiState = viewModel.uiState.collectAsState().value
        val bannerUiState = viewModel.bannerState.collectAsState().value
        val homeItems = viewModel.homeList.collectAsState().value.orEmpty()
        val authState = bcscAuthViewModel.authStatus.collectAsState().value

        MyHealthTheme {
            Scaffold(
                topBar = { HomeToolbar(authState.loginStatus != LoginStatus.NOT_AUTHENTICATED) },
                content = {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        HomeScreen(
                            Modifier
                                .statusBarsPadding()
                                .navigationBarsPadding()
                                .padding(it),
                            homeViewModel,
                            onClickManage = {},
                            onQuickAccessTileClicked = {}
                        )
                    }
                },
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
            )
        }
    }

    @Composable
    private fun HomeToolbar(isAuthenticated: Boolean) {
        MyHealthToolBar(
            title = "",
            actions = {

                if (isAuthenticated) {
                    IconButton(
                        onClick = { findNavController().navigate(R.id.notificationFragment) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notification),
                            contentDescription = stringResource(id = R.string.notifications),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
                IconButton(
                    onClick = { findNavController().navigate(R.id.settingsFragment) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = stringResource(
                            id = R.string.settings
                        ),
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        )
    }

    override fun handleBCSCAuthState(bcscAuthState: BcscAuthState) {
        if (bcscAuthState == BcscAuthState.SUCCESS) {
            if (sharedViewModel.destinationId > 0) {
                findNavController().navigate(sharedViewModel.destinationId)
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

    private fun updateHomeRecordsList() {
        launchOnStart { viewModel.getHomeRecordsList() }
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

            if (uiState.isForceLogout) {
                bcscAuthViewModel.getEndSessionIntent()
                viewModel.onForceLogout(false)
            }

            if (uiState.displayServiceDownMessage) {
                view?.showServiceDownMessage(requireContext())
                viewModel.resetUiState()
            }
        }
    }

    private fun onClickLearnMore(banner: BannerItem) {
        val action = HomeFragmentDirections.actionHomeFragmentToBannerDetail(
            title = banner.title,
            date = banner.date,
            body = banner.body,
        )
        findNavController().navigate(action)
    }

    @BasePreview
    @Composable
    private fun PreviewHomeToolbar() {
        MyHealthTheme {
            HomeToolbar(true)
        }
    }

    @BasePreview
    @Composable
    private fun PreviewHomeToolbarNonAuthenticated() {
        MyHealthTheme {
            HomeToolbar(false)
        }
    }
}
