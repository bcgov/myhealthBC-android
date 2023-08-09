package ca.bc.gov.bchealth.ui.recommendations

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
import ca.bc.gov.bchealth.databinding.FragmentRecommendationsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendationsFragment : BaseFragment(null) {
    private val binding by viewBindings(FragmentRecommendationsBinding::bind)

    private val viewModel: RecommendationsViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
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
                        title = stringResource(id = R.string.recommendations_home_title),
                        actionItems = menuItems
                    )
                },
                content = {
                    RecommendationScreen(
                        Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                    )
                }
            )
        }
    }
}
