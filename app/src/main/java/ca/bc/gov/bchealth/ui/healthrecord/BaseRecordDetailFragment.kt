package ca.bc.gov.bchealth.ui.healthrecord

import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.healthrecord.comment.RecordCommentsAdapter
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.widget.AddCommentCallback
import ca.bc.gov.bchealth.widget.AddCommentLayout
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.SYNC_COMMENTS

abstract class BaseRecordDetailFragment(@LayoutRes id: Int) : BaseFragment(id) {
    private lateinit var recordCommentsAdapter: RecordCommentsAdapter
    private val commentsViewModel: CommentsViewModel by viewModels()

    abstract fun getCommentEntryTypeCode(): CommentEntryTypeCode
    abstract fun getParentEntryId(): String?
    abstract fun getProgressBar(): View
    abstract fun getCommentView(): AddCommentLayout

    fun initCommentView() = with(getCommentView()) {
        toggleVisibility(BuildConfig.FLAG_ADD_COMMENTS)
        addCommentListener(object : AddCommentCallback {
            override fun onSubmitComment(commentText: String) {
                getParentEntryId()?.let {
                    commentsViewModel.addComment(
                        it,
                        commentText,
                        getCommentEntryTypeCode().value,
                    )
                }
            }
        })
    }

    suspend fun observeComments() {
        commentsViewModel.uiState.collect { state ->
            getProgressBar().isVisible = state.onLoading

            if (state.latestComment.isNotEmpty()) {
                recordCommentsAdapter.submitList(state.latestComment)
                if (BuildConfig.FLAG_ADD_COMMENTS) {
                    getCommentView().clearComment()
                }
            }

            handleError(state.onError)
        }
    }

    fun observeCommentsSyncCompletion() {
        observeWork(SYNC_COMMENTS) {
            if (it == WorkInfo.State.SUCCEEDED) {
                getParentEntryId()?.let { parentEntryId ->
                    commentsViewModel.getComments(parentEntryId)
                }
            }
        }
    }

    fun getRecordCommentsAdapter(): RecordCommentsAdapter {
        recordCommentsAdapter = RecordCommentsAdapter(::navigateToComments)
        return recordCommentsAdapter
    }

    private fun navigateToComments(commentEntryTypeCode: String) {
        findNavController().navigate(
            R.id.commentsFragment,
            bundleOf("parentEntryId" to commentEntryTypeCode)
        )
    }

    open fun handleError(isFailed: Boolean) {
        if (isFailed) {
            showGenericError()
        }
    }
}
