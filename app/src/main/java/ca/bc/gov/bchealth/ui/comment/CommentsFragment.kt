package ca.bc.gov.bchealth.ui.comment

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
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

        val uiState = viewModel.uiState.collectAsState().value

        if (uiState.isBcscSessionActive == false) findNavController().navigate(R.id.individualHealthRecordFragment)

        MyHealthScaffold(
            title = stringResource(id = R.string.comments),
            isLoading = uiState.onLoading,
            navigationAction = ::popNavigation,
        ) {
            CommentsScreen(
                uiState,
                ::onTapEdit,
                ::onTapDelete,
                ::onTapSubmit,
                ::onTapUpdate,
                ::onTapCancelUpdate,
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (FLAG_ADD_COMMENTS) {
            observeComments()
            viewModel.getComments(args.parentEntryId)
            observeCommentsSyncCompletion()
        }
    }

    private fun onTapEdit(comment: Comment) {
        viewModel.toggleEditMode(true)
        comment.editable = true
    }

    private fun onTapDelete(comment: Comment) {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.comments_alert_delete_title),
            msg = getString(R.string.comments_alert_delete_body),
            positiveBtnMsg = getString(R.string.delete),
            negativeBtnMsg = getString(R.string.cancel),
            positiveBtnCallback = {
                viewModel.deleteComment(args.parentEntryId, comment)
            },
            cancelable = true
        )
    }

    private fun onTapSubmit(commentText: String) {
        viewModel.addComment(
            args.parentEntryId,
            commentText,
            args.recordType
        )
    }

    private fun onTapUpdate(comment: Comment) {
        viewModel.updateComment(
            args.parentEntryId,
            comment,
        )
    }

    private fun onTapCancelUpdate(comment: Comment) {
        comment.editable = false
        viewModel.toggleEditMode(false)
    }

    private fun observeComments() {
        launchOnStart {
            viewModel.uiState.collect { state ->
                when {
                    state.onError -> {
                        showError()
                        viewModel.resetUiState()
                    }
                    state.commentsList != null && state.commentsList.isEmpty() -> popNavigation()
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
