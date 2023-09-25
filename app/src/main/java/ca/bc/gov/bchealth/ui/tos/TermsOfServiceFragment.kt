package ca.bc.gov.bchealth.ui.tos

import android.os.Bundle
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.component.HGCenterAlignedTopAppBar
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class TermsOfServiceFragment : BaseFragment(null) {

    private val termsOfServiceViewModel: TermsOfServiceViewModel by activityViewModels()

    companion object {
        const val TERMS_OF_SERVICE_STATUS = "TERMS_OF_SERVICE_STATUS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findNavController().previousBackStackEntry?.savedStateHandle
            ?.set(TERMS_OF_SERVICE_STATUS, TermsOfServiceStatus.DECLINED)
    }

    @Composable
    override fun GetComposableLayout() {
        HealthGatewayTheme {
            Scaffold(
                topBar = {
                    HGCenterAlignedTopAppBar(
                        onNavigationAction = { findNavController().popBackStack() },
                        title = stringResource(id = R.string.terms_of_service)
                    )
                },
                content = {
                    TermsOfServiceScreen(
                        onTermsOfServiceStatusChanged = :: onTermsOfServiceStatusChanged,
                        Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        viewModel = termsOfServiceViewModel
                    )
                }
            )
        }
    }

    private fun onTermsOfServiceStatusChanged(termsOfServiceStatus: TermsOfServiceStatus) {
        findNavController().previousBackStackEntry?.savedStateHandle
            ?.set(TERMS_OF_SERVICE_STATUS, termsOfServiceStatus)
        findNavController().popBackStack()
    }
}

enum class TermsOfServiceStatus {
    ACCEPTED,
    DECLINED
}
