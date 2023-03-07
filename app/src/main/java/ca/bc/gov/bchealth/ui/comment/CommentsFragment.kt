package ca.bc.gov.bchealth.ui.comment

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.common.BuildConfig.FLAG_ADD_COMMENTS
import ca.bc.gov.repository.SYNC_COMMENTS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : BaseFragment(null) {
    private val args: CommentsFragmentArgs by navArgs()
    private val viewModel: CommentsViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        CommentsUI(::popNavigation, viewModel, {}, {})
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (FLAG_ADD_COMMENTS) {
            addCommentListener()
            observeComments()
            viewModel.getComments(args.parentEntryId)
            observeCommentsSyncCompletion()
        }
    }

    private fun addCommentListener() {
        binding.comment.addCommentListener(object : AddCommentCallback {
            override fun onSubmitComment(commentText: String) {
                viewModel.addComment(
                    args.parentEntryId,
                    commentText,
                    CommentEntryTypeCode.MEDICATION.value
                )
            }
        })
    }

    private fun observeComments() {
        launchOnStart {
            viewModel.uiState.collect { state ->
                if (state.commentsList.isNotEmpty()) {
                    commentsAdapter.submitList(state.commentsList) {
                        binding.rvCommentsList.scrollToBottom()
                    }
                    viewModel.resetUiState()
                    // clear comment
                    binding.comment.clearComment()
                }

                if (state.onError) {
                    showError()
                    viewModel.resetUiState()
                }
            }
        }
    }

    private fun showError() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.error),
            msg = getString(R.string.error_message),
            positiveBtnMsg = getString(R.string.dialog_button_ok),
            positiveBtnCallback = {
                findNavController().popBackStack()
            }
        )
    }

    private fun observeCommentsSyncCompletion() {
        observeWork(SYNC_COMMENTS) { state ->
            if (state == WorkInfo.State.SUCCEEDED) {
                viewModel.getComments(args.parentEntryId)
            }
        }
    }
}
