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
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthToolbar
import ca.bc.gov.bchealth.utils.redirect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendationsFragment : BaseFragment(null) {
    private val viewModel: RecommendationsViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        HealthGatewayTheme {
            Scaffold(
                topBar = {
                    MyHealthToolbar(
                        navigationAction = { findNavController().popBackStack() },
                        title = stringResource(id = R.string.recommendations_home_title)
                    )
                },
                content = {
                    RecommendationScreen(
                        Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        viewModel = viewModel,
                        onLinkClicked = ::onLinkClicked
                    )
                }
            )
        }
    }

    private fun onLinkClicked() {
        requireContext().redirect("https://immunizebc.ca/")
    }
}
