package ca.bc.gov.bchealth.ui.home

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.HomeDirections
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.component.HGTopAppBar
import ca.bc.gov.bchealth.compose.component.menu.TopAppBarActionItem
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BcscAuthState
import ca.bc.gov.bchealth.ui.NavigationAction
import ca.bc.gov.bchealth.ui.auth.BioMetricState
import ca.bc.gov.bchealth.ui.auth.BiometricsAuthenticationFragment
import ca.bc.gov.bchealth.ui.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.ui.healthrecord.filter.PatientFilterViewModel
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.utils.observeCurrentBackStackForAction
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import ca.bc.gov.common.model.UserAuthenticationStatus
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class HomeFragment : BaseSecureFragment(null) {

    private val viewModel: HomeViewModel by viewModels()
    private val authViewModel: BcscAuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val filterSharedViewModel: PatientFilterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        observeCurrentBackStackForAction<BioMetricState>(BiometricsAuthenticationFragment.BIOMETRIC_STATE) {
            viewModel.onBiometricAuthenticationCompleted()
            when (it) {
                BioMetricState.SUCCESS -> {
                    sharedViewModel.shouldFetchBanner = true
                    findNavController().currentBackStackEntry?.savedStateHandle?.remove<BioMetricState>(
                        BiometricsAuthenticationFragment.BIOMETRIC_STATE
                    )
                    viewModel.executeOneTimeDataFetch()
                }

                else -> {
                    findNavController().popBackStack()
                }
            }
        }
    }

    @Composable
    override fun GetComposableLayout() {
        val userAuthState =
            authViewModel.userAuthenticationState.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED).value

        val authState = authViewModel.authStatus.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED).value
        val menuItems = mutableListOf<TopAppBarActionItem>(
            TopAppBarActionItem.IconActionItem.AlwaysShown(
                title = getString(R.string.settings),
                onClick = { findNavController().navigate(R.id.settingsFragment) },
                icon = R.drawable.ic_menu_settings,
                contentDescription = getString(R.string.settings),
            )
        )

        if (userAuthState == UserAuthenticationStatus.AUTHENTICATED) {
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
                        onQuickAccessTileClicked = ::onQuickAccessTileClicked,
                        onMoreActionClick = ::onMoreActionClicked
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
            BcscAuthState.NOT_NOW -> {
            }
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
        when (quickAccessTileItem) {
            is QuickAccessTileItem.QuickLinkTileItem -> {
                quickAccessTileItem.payload?.let {
                    filterSharedViewModel.updateFilter(
                        listOf(TimelineTypeFilter.findByFilterValue(it).name)
                    )
                }
            }

            else -> {
                filterSharedViewModel.clearFilter()
            }
        }
        findNavController().navigate(quickAccessTileItem.destinationId)
    }

    private fun onManageClicked() {
        findNavController().navigate(R.id.quickAccessManagementFragment)
    }

    private fun onMoreActionClicked(id: Long, name: String) {
        val action =
            HomeDirections.actionGlobalRemoveQuickAccessTileBottomSheetFragment(id, name)
        findNavController().navigate(action)
    }
}
