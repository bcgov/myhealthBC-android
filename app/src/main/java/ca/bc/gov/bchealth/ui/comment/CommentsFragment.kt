package ca.bc.gov.bchealth.ui.comment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentCommentsBinding
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentsFragment : Fragment(R.layout.fragment_comments) {

    private val binding by viewBindings(FragmentCommentsBinding::bind)
    private val args: CommentsFragmentArgs by navArgs()
    private val viewModel: CommentsViewModel by viewModels()
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observeComments()
        viewModel.getComments(args.parentEntryId)
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

    private fun observeComments() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.progressBar.isVisible = state.onLoading

                    if (state.commentsList != null) {
                        commentsAdapter.submitList(state.commentsList)
                        viewModel.resetUiState()
                    }

                    if (state.onError) {
                        showError()
                        viewModel.resetUiState()
                    }
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

    private fun addCommentListener() {
        binding.comment.tipComment.editText?.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank()) {
                binding.comment.tipComment.setEndIconDrawable(R.drawable.ic_add_comment)
            } else {
                binding.comment.tipComment.apply {
                    setEndIconDrawable(R.drawable.ic_add_comment_press)
                    setEndIconTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.blue
                            )
                        )
                    )
                }
            }
        }
        binding.comment.tipComment.setEndIconOnClickListener {
            if (!binding.comment.edComment.text.isNullOrBlank()) {
                viewModel.addComment(
                    args.parentEntryId,
                    args.userProfileId,
                    binding.comment.edComment.text.toString()
                )
            }
        }
    }
}
