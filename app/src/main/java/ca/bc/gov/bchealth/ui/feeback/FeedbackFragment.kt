package ca.bc.gov.bchealth.ui.feeback

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import ca.bc.gov.bchealth.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackFragment : BaseFragment(null) {
    private val feedbackViewModel: FeedbackViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        FeedbackUI(
            navigationAction = ::popNavigation,
            sendAction = ::onClickSend
        )
    }

    private fun onClickSend(message: String) {
        feedbackViewModel.sendMessage(message)
    }
}
