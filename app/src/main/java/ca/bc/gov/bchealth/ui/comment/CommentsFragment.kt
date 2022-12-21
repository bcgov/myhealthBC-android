package ca.bc.gov.bchealth.ui.comment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentCommentsBinding
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.scrollToBottom
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.widget.AddCommentCallback
import ca.bc.gov.common.BuildConfig.FLAG_ADD_COMMENTS
import ca.bc.gov.repository.SYNC_COMMENTS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : Fragment(R.layout.fragment_comments) {

    private val binding by viewBindings(FragmentCommentsBinding::bind)
    private val args: CommentsFragmentArgs by navArgs()
    private val viewModel: CommentsViewModel by viewModels()
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (FLAG_ADD_COMMENTS) {
            initUi()
            observeComments()
            viewModel.getComments(args.parentEntryId)
            observeCommentsSyncCompletion()
        }
    }

    private fun initUi() {
        setUpToolBar()
        setUpRecyclerView()
        addCommentListener()
    }

    private fun setUpRecyclerView() {
        commentsAdapter = CommentsAdapter()
        val recyclerView = binding.rvCommentsList
        recyclerView.adapter = commentsAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.comments)

            line1.visibility = View.VISIBLE
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

                binding.progressBar.isVisible = state.onLoading

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
        val workRequest = WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(SYNC_COMMENTS)
        if (!workRequest.hasObservers()) {
            workRequest.observe(viewLifecycleOwner) {
                if (it.firstOrNull()?.state == WorkInfo.State.SUCCEEDED) {
                    viewModel.getComments(args.parentEntryId)
                }
            }
        }
    }
}
