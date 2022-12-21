package ca.bc.gov.bchealth.ui.healthrecord

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
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.healthrecord.comment.RecordCommentsAdapter
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.bchealth.utils.scrollToBottom
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
    open fun getProgressBar(): View? = null
    abstract fun getCommentView(): AddCommentLayout

    fun initCommentView() = with(getCommentView()) {
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
    }

    open fun getRecyclerView(): RecyclerView? = null
    open fun getScrollView(): NestedScrollView? = null

    fun observeComments() {
        if (BuildConfig.FLAG_ADD_COMMENTS.not()) return

        launchOnStart {
            commentsViewModel.uiState.collect { state ->
                getProgressBar()?.isVisible = state.onLoading

                if (state.latestComment.isNotEmpty()) {
                    recordCommentsAdapter.submitList(state.latestComment) {
                        if (state.onCommentsUpdated) {
                            getRecyclerView()?.scrollToBottom()
                            getScrollView()?.let { it.post { it.fullScroll(View.FOCUS_DOWN) } }
                        }
                    }
                    getCommentView().clearComment()
                }

                handleError(state.onError)
            }
        }
    }

    fun observeCommentsSyncCompletion() {
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
