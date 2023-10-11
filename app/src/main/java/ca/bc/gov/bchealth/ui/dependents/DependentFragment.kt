package ca.bc.gov.bchealth.ui.dependents

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.BcVaccineCardNavGraphDirections
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.component.HGTopAppBar
import ca.bc.gov.bchealth.compose.component.menu.TopAppBarActionItem
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.model.BcServiceCardLoginInfoType
import ca.bc.gov.bchealth.model.BcServiceCardSessionInfoType
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.NavigationAction
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author pinakin.kansara
 * Created 2023-10-10 at 1:04 p.m.
 */
@AndroidEntryPoint
class DependentFragment : BaseSecureFragment(null) {
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val viewModel: DependentsViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        val authState =
            bcscAuthViewModel.authStatus.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED).value
        val menuItems = mutableListOf<TopAppBarActionItem>(
            TopAppBarActionItem.IconActionItem.AlwaysShown(
                title = getString(R.string.settings),
                onClick = { findNavController().navigate(R.id.settingsFragment) },
                icon = R.drawable.ic_menu_settings,
                contentDescription = getString(R.string.settings),
            )
        )
        HealthGatewayTheme {
            Scaffold(
                topBar = {
                    HGTopAppBar(
                        title = "",
                        actionItems = menuItems,
                        elevation = 0.dp
                    )
                },
                content = {
                    DependentsScreen(
                        onRequireAuthentication = ::onRequireAuthentication,
                        onAddDependentClick = ::onAddDependentClick,
                        onManageDependentClick = ::onManageDependentClick,
                        onDependentClick = ::onClickDependent,
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        authViewModel = bcscAuthViewModel,
                        viewModel = viewModel
                    )
                }
            )
        }
    }

    override fun handleNavigationAction(navigationAction: NavigationAction) {
        when (navigationAction) {
            NavigationAction.ACTION_BACK -> {
                findNavController().popBackStack()
            }

            NavigationAction.ACTION_RE_CHECK -> {
                bcscAuthViewModel.checkSession()
            }
        }
    }

    private fun onRequireAuthentication(loginStatus: LoginStatus) {
        when (loginStatus) {
            LoginStatus.ACTIVE -> {
                // No operation
            }

            LoginStatus.EXPIRED -> {
                val action =
                    BcVaccineCardNavGraphDirections.actionGlobalBcServiceCardSessionFragment(
                        BcServiceCardSessionInfoType.DEPENDENTS
                    )
                findNavController().navigate(action)
            }

            LoginStatus.NOT_AUTHENTICATED -> {
                val action =
                    BcVaccineCardNavGraphDirections.actionGlobalBcServicesCardLoginFragment(
                        BcServiceCardLoginInfoType.DEPENDENTS
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun onAddDependentClick() {
        findNavController().navigate(
            R.id.addDependentFragment,
            null
        )
    }

    private fun onManageDependentClick() {
        findNavController().navigate(
            R.id.manageDependentFragment,
            null
        )
    }

    private fun onClickDependent(dependent: DependentDetailItem) {
        findNavController().navigate(
            R.id.dependentRecordsFragment,
            bundleOf(
                "patientId" to dependent.patientId,
                "hdid" to dependent.hdid,
                "fullName" to dependent.fullName
            )
        )
    }
}
