package ca.bc.gov.bchealth.ui.home

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.component.HGTopAppBar
import ca.bc.gov.bchealth.compose.component.menu.TopAppBarActionItem
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeComposeFragment : BaseSecureFragment(null) {

    private val viewModel: HomeComposeViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        val menuItems = listOf<TopAppBarActionItem>(
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
                        title = stringResource(id = R.string.home),
                        actionItems = menuItems
                    )
                },
                content = {
                    HomeScreen(
                        Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        viewModel,
                        onQuickAccessTileClicked = ::onQuickAccessTileClicked
                    )
                }
            )
        }
    }

    private fun onQuickAccessTileClicked(quickAccessTileItem: QuickAccessTileItem) {
        findNavController().navigate(quickAccessTileItem.destinationId)
    }
}
