package ca.bc.gov.bchealth.ui.home

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.component.HGTopAppBar
import ca.bc.gov.bchealth.compose.component.menu.TopAppBarActionItem
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BcscAuthState
import ca.bc.gov.bchealth.ui.NavigationAction
import ca.bc.gov.bchealth.ui.auth.BioMetricState
import ca.bc.gov.bchealth.ui.auth.BiometricsAuthenticationFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.observeCurrentBackStackForAction
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseSecureFragment(null) {

    private val viewModel: HomeViewModel by viewModels()
    private val authViewModel: BcscAuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        observeCurrentBackStackForAction<BioMetricState>(BiometricsAuthenticationFragment.BIOMETRIC_STATE) {
            viewModel.onBiometricAuthenticationCompleted()
            when (it) {
                BioMetricState.SUCCESS -> {
                    sharedViewModel.shouldFetchBanner = true
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<BioMetricState>(
                        BiometricsAuthenticationFragment.BIOMETRIC_STATE
                    )
                }

                else -> {
                    findNavController().popBackStack()
                }
            }
        }
    }

    @Composable
    override fun GetComposableLayout() {
        println("Home: ComposeCreated")
        val authState = authViewModel.authStatus.collectAsState().value
        val menuItems = mutableListOf<TopAppBarActionItem>(
            TopAppBarActionItem.IconActionItem.AlwaysShown(
                title = getString(R.string.settings),
                onClick = { findNavController().navigate(R.id.settingsFragment) },
                icon = R.drawable.ic_menu_settings,
                contentDescription = getString(R.string.settings),
            )
        )

        authState.loginStatus?.let {
            if (it == LoginStatus.ACTIVE) {
                menuItems.add(
                    0,
                    TopAppBarActionItem.IconActionItem.AlwaysShown(
                        title = getString(R.string.notifications),
                        onClick = { findNavController().navigate(R.id.notificationFragment) },
                        icon = R.drawable.ic_notification,
                        contentDescription = getString(R.string.notifications),
                    )
                )
            }
        }
        HealthGatewayTheme {
            Scaffold(
                topBar = {
                    HGTopAppBar(
                        title = authState.userName ?: stringResource(id = R.string.home),
                        actionItems = menuItems
                    )
                },
                content = {
                    HomeScreen(
                        Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        authViewModel = authViewModel,
                        viewModel = viewModel,
                        sharedViewModel = sharedViewModel,
                        onLoginClick = ::onLoginClick,
                        onManageClick = ::onManageClicked,
                        onOnBoardingRequired = ::onOnBoardingRequired,
                        onBiometricAuthenticationRequired = ::onBiometricAuthenticationRequired,
                        onQuickAccessTileClicked = ::onQuickAccessTileClicked
                    )
                }
            )
        }
    }

    override fun handleBCSCAuthState(bcscAuthState: BcscAuthState) {
        viewModel.resetUIState()
        when (bcscAuthState) {
            BcscAuthState.SUCCESS -> {
                if (sharedViewModel.destinationId > 0) {
                    findNavController().navigate(sharedViewModel.destinationId)
                }
            }
            BcscAuthState.NO_ACTION,
            BcscAuthState.NOT_NOW -> {}
        }
    }

    override fun handleNavigationAction(navigationAction: NavigationAction) {
        when (navigationAction) {
            NavigationAction.ACTION_BACK -> {
                findNavController().popBackStack()
            }

            NavigationAction.ACTION_RE_CHECK -> {
                authViewModel.checkSession()
            }
        }
    }

    private fun onLoginClick() {
        sharedViewModel.destinationId = 0
        findNavController().navigate(R.id.bcscAuthInfoFragment)
    }

    private fun onBiometricAuthenticationRequired() {
        if (!sharedViewModel.isBiometricAuthShown) {
            findNavController().navigate(R.id.biometricsAuthenticationFragment)
            sharedViewModel.isBiometricAuthShown = true
        }
        viewModel.onBiometricAuthenticationCompleted()
    }

    private fun onOnBoardingRequired(isReOnBoarding: Boolean) {
        findNavController().navigate(
            R.id.onBoardingSliderFragment,
            bundleOf("reOnBoardingRequired" to isReOnBoarding)
        )
    }
    private fun onQuickAccessTileClicked(quickAccessTileItem: QuickAccessTileItem) {
        findNavController().navigate(quickAccessTileItem.destinationId)
    }

    private fun onManageClicked() {
        findNavController().navigate(R.id.quickAccessManagementFragment)
    }
}
