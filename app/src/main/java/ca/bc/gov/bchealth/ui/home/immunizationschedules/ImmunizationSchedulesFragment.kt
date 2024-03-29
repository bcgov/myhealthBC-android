package ca.bc.gov.bchealth.ui.home.immunizationschedules

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
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthToolbar
import ca.bc.gov.bchealth.utils.redirect

class ImmunizationSchedulesFragment : BaseFragment(null) {

    private val viewModel: ImmunizationSchedulesViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        MyHealthTheme {
            Scaffold(
                topBar = {
                    MyHealthToolbar(
                        title = stringResource(id = R.string.immnz_schedules_title),
                        navigationAction = { findNavController().popBackStack() }
                    )
                },
                content = {
                    ImmunizationSchedulesScreen(
                        viewModel = viewModel,
                        onClickItem = ::onClickUrl,
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it)
                    )
                },
            )
        }
    }

    private fun onClickUrl(url: String) {
        context?.redirect(url)
    }
}
