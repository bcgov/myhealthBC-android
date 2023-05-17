package ca.bc.gov.bchealth.ui.healthrecord.healthvisits

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsUiState
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.SYNC_COMMENTS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HealthVisitDetailFragment : BaseFragment(null) {
    private val viewModel: HealthVisitViewModel by viewModels()
    private val args: HealthVisitDetailFragmentArgs by navArgs()
    private val commentsViewModel: CommentsViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        val uiState = viewModel.uiState.collectAsState().value
        var commentState: CommentsUiState? = null

        if (BuildConfig.FLAG_ADD_COMMENTS) {
            uiState.parentEntryId?.let { commentsViewModel.getComments(it) }
            commentState = commentsViewModel.uiState.collectAsState().value
        }

        MyHealthScaffold(
            title = uiState.title,
            isLoading = uiState.onLoading,
            navigationAction = ::popNavigation
        ) {
            HealthVisitDetailScreen(
                uiState = uiState,
                onClickFaq = { requireContext().redirect(getString(R.string.faq_link)) },
                onClickComments = ::navigateToComments,
                commentsSummary = commentState?.commentsSummary,
                onSubmitComment = ::onSubmitComment
            )
        }

        if (uiState.onError) {
            showGenericError()
            viewModel.resetUiState()
        }
        if (commentState?.isBcscSessionActive == false) popNavigation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchHealthVisitDetails(args.healthVisitRecordId)
        observeCommentsSyncCompletion()
    }

    private fun onSubmitComment(content: String) {
        viewModel.getParentEntryId()?.let { parentEntryId ->
            commentsViewModel.addComment(
                parentEntryId,
                content,
                CommentEntryTypeCode.HEALTH_VISITS.value,
            )
        }
    }

    private fun observeCommentsSyncCompletion() {
        if (BuildConfig.FLAG_ADD_COMMENTS.not()) return

        observeWork(SYNC_COMMENTS) {
            if (it == WorkInfo.State.SUCCEEDED) {
                viewModel.getParentEntryId()?.let { parentId ->
                    commentsViewModel.getComments(parentId)
                }
            }
        }
    }

    private fun navigateToComments(commentsSummary: CommentsSummary) {
        findNavController().navigate(
            R.id.commentsFragment,
            bundleOf(
                "parentEntryId" to commentsSummary.parentEntryId,
                "recordType" to commentsSummary.entryTypeCode,
            )
        )
    }
}
