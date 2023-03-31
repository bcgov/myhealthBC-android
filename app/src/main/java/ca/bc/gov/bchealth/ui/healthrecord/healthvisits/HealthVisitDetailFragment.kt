package ca.bc.gov.bchealth.ui.healthrecord.healthvisits

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.utils.redirect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HealthVisitDetailFragment : BaseFragment(null) {

    private val viewModel: HealthVisitViewModel by viewModels()
    private val args: HealthVisitDetailFragmentArgs by navArgs()

    @Composable
    override fun GetComposableLayout() {
        val uiState = viewModel.uiState.collectAsState().value

        MyHealthScaffold(
            title = uiState.title,
            isLoading = uiState.onLoading,
            navigationAction = ::popNavigation
        ) {
            HealthVisitDetailScreen(
                uiState,
                { requireContext().redirect(getString(R.string.faq_link)) },
                {}
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeHealthVisitDetails()
        viewModel.fetchHealthVisitDetails(args.healthVisitRecordId)
    }

    private fun observeHealthVisitDetails() {
        viewModel.uiState.collectOnStart { state ->
            if (state.onError) {
                showGenericError()
                viewModel.resetUiState()
            }
        }
    }
}
