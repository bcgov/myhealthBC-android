package ca.bc.gov.bchealth.ui.healthrecord

import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.healthrecord.comment.RecordCommentsAdapter
import ca.bc.gov.bchealth.utils.hideKeyboard
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.bchealth.utils.scrollToBottom
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.widget.AddCommentCallback
import ca.bc.gov.bchealth.widget.AddCommentLayout
import ca.bc.gov.bchealth.widget.CommentFocusChangeCallback
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.SYNC_COMMENTS

abstract class BaseRecordDetailFragment(@LayoutRes id: Int) : BaseFragment(id) {
    private lateinit var recordCommentsAdapter: RecordCommentsAdapter
    private val commentsViewModel: CommentsViewModel by viewModels()

    abstract fun getCommentEntryTypeCode(): CommentEntryTypeCode
    abstract fun getParentEntryId(): String?
    abstract fun getCommentView(): AddCommentLayout

    open fun getProgressBar(): View? = null
    open fun getScrollableView(): View? = null

    fun initComments() {
        initCommentView()
        observeComments()
        observeCommentsSyncCompletion()
    }

    private fun initCommentView() = with(getCommentView()) {
        toggleVisibility(BuildConfig.FLAG_ADD_COMMENTS)

        if (BuildConfig.FLAG_ADD_COMMENTS.not()) return

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

        addFocusChangeListener(object : CommentFocusChangeCallback {
            override fun onFocusChange(hasFocus: Boolean) {
                if (hasFocus) {
                    getScrollableView()?.setOnTouchListener { view, _ ->
                        requireContext().hideKeyboard(view)
                        view?.clearFocus()
                        this@with.clearFocus()
                        return@setOnTouchListener false
                    }
                }
            }
        })
    }

    private fun observeComments() {
        if (BuildConfig.FLAG_ADD_COMMENTS.not()) return

        launchOnStart {
            commentsViewModel.uiState.collect { state ->
                getProgressBar()?.isVisible = state.onLoading

                val list = if (state.commentsSummary != null) {
                    listOf(state.commentsSummary)
                } else {
                    listOf()
                }

                recordCommentsAdapter.submitList(list) {
                    if (state.onCommentsUpdated) {
                        scrollToBottom()
                    }
                }
                getCommentView().clearComment()

                handleError(state.onError)
            }
        }
    }

    private fun observeCommentsSyncCompletion() {
        if (BuildConfig.FLAG_ADD_COMMENTS.not()) return

        observeWork(SYNC_COMMENTS) {
            if (it == WorkInfo.State.SUCCEEDED) {
                getComments(getParentEntryId())
            }
        }
    }

    fun getComments(parentEntryId: String?) {
        parentEntryId?.let {
            commentsViewModel.getComments(it)
        }
    }

    fun getRecordCommentsAdapter(): RecordCommentsAdapter {
        recordCommentsAdapter = RecordCommentsAdapter(::navigateToComments)
        return recordCommentsAdapter
    }

    private fun scrollToBottom() {
        when (val scrollableView = getScrollableView()) {
            is NestedScrollView -> scrollableView.scrollToBottom()
            is RecyclerView -> scrollableView.scrollToBottom()
            null -> logWarning("scrollableView is null, did you override getScrollableView ?")
            else -> logWarning("Scroll action for View: $scrollableView is not implemented yet, skipping")
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

    private fun logWarning(message: String) {
        Log.w("BaseRecordDetailFragment", message)
    }

    open fun handleError(isFailed: Boolean) {
        if (isFailed) {
            showGenericError()
        }
    }
}
