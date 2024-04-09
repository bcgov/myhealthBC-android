package ca.bc.gov.bchealth.ui.healthrecord.labtest.help

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.utils.redirect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LabTestHelpFragment : BaseFragment(null) {

    @Composable
    override fun GetComposableLayout() {
        MyHealthScaffold(
            title = stringResource(id = R.string.feedback_title),
            navigationAction = { findNavController().popBackStack() },
        ) {
            LabTestHelpContent(::openLink)
        }
    }

    private fun openLink(url: String) {
        requireContext().redirect(url)
    }
}
