package ca.bc.gov.bchealth.ui.healthrecord.medication

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMedicationDetailsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.healthrecord.comment.RecordCommentsAdapter
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.widget.AddCommentCallback
import ca.bc.gov.common.BuildConfig.FLAG_ADD_COMMENTS
import ca.bc.gov.repository.SYNC_COMMENTS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicationDetailsFragment : BaseFragment(R.layout.fragment_medication_details) {

    private val binding by viewBindings(FragmentMedicationDetailsBinding::bind)
    private val args: MedicationDetailsFragmentArgs by navArgs()
    private val viewModel: MedicationDetailsViewModel by viewModels()
    private lateinit var medicationDetailAdapter: MedicationDetailAdapter
    private lateinit var recordCommentsAdapter: RecordCommentsAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private val commentsViewModel: CommentsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        if (medicationDetailAdapter.currentList.isEmpty()) {
            viewModel.getMedicationDetails(args.medicationId)
        }
        launchOnStart { observeUiState() }
        launchOnStart { observeComments() }
        observeCommentsSyncCompletion()
    }

    private fun initUI() {
        setUpRecyclerView()
        binding.comment.toggleVisibility(FLAG_ADD_COMMENTS)
        addCommentListener()
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setUpRecyclerView() {
        medicationDetailAdapter = MedicationDetailAdapter()
        initCommentsAdapter()
        concatAdapter = ConcatAdapter(medicationDetailAdapter, recordCommentsAdapter)

        val recyclerView = binding.rvMedicationDetailList
        recyclerView.adapter = concatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initCommentsAdapter() {
        recordCommentsAdapter = RecordCommentsAdapter { parentEntryId ->
            val action = MedicationDetailsFragmentDirections
                .actionMedicationDetailsFragmentToCommentsFragment(
                    parentEntryId
                )
            findNavController().navigate(action)
        }
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { state ->

            binding.progressBar.isVisible = state.onLoading

            if (state.medicationDetails?.isNotEmpty() == true) {
                medicationDetailAdapter.submitList(state.medicationDetails)
                binding.layoutToolbar.topAppBar.title = state.toolbarTitle
            }

            handleError(state.onError)

            getComments()
        }
    }

    private fun getComments() {
        viewModel.uiState.value.parentEntryId?.let {
            commentsViewModel.getComments(it)
        }
    }

    private suspend fun observeComments() {
        commentsViewModel.uiState.collect { state ->
            binding.progressBar.isVisible = state.onLoading

            if (state.latestComment.isNullOrEmpty().not()) {
                recordCommentsAdapter.submitList(state.latestComment)
                // clear comment
                if (FLAG_ADD_COMMENTS) {
                    binding.comment.clearComment()
                }
            }

            handleError(state.onError)
        }
    }

    private fun handleError(isFailed: Boolean) {
        if (isFailed) {
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
    }

    private fun addCommentListener() {
        binding.comment.addCommentListener(object : AddCommentCallback {
            override fun onSubmitComment(commentText: String) {
                viewModel.uiState.value.parentEntryId?.let {
                    commentsViewModel.addComment(
                        it,
                        commentText,
                        CommentEntryTypeCode.MEDICATION.value,
                    )
                }
            }
        })
    }

    private fun observeCommentsSyncCompletion() {
        observeWork(SYNC_COMMENTS) {
            if (it == WorkInfo.State.SUCCEEDED) {
                viewModel.uiState.value.parentEntryId?.let { parentEntryId ->
                    commentsViewModel.getComments(
                        parentEntryId
                    )
                }
            }
        }
    }
}
