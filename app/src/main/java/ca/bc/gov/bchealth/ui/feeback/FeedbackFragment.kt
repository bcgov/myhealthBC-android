package ca.bc.gov.bchealth.ui.feeback

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.showErrorSnackbar
import ca.bc.gov.bchealth.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackFragment : BaseFragment(null) {
    private val feedbackViewModel: FeedbackViewModel by viewModels()

    override fun getBaseViewModel() = feedbackViewModel

    @Composable
    override fun GetComposableLayout() {
        FeedbackUI(
            uiStateFlow = feedbackViewModel.uiState,
            navigationAction = { findNavController().popBackStack() },
            sendAction = ::onClickSend,
            onMessageSent = ::onMessageSent,
            onError = ::displayError,
        )
    }

    private fun onMessageSent() {
        requireActivity().toast(getString(R.string.feedback_success_message))
        feedbackViewModel.resetUiState()
    }

    private fun onClickSend(message: String) {
        feedbackViewModel.sendMessage(message)
    }

    private fun displayError() {
        view.showErrorSnackbar(getString(R.string.feedback_error_message))
        feedbackViewModel.resetUiState()
    }
}
