package ca.bc.gov.bchealth.ui.healthrecord.medication

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMedicationDetailsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.widget.AddCommentCallback
import ca.bc.gov.repository.SYNC_COMMENTS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MedicationDetailsFragment : BaseFragment(R.layout.fragment_medication_details) {

    private val binding by viewBindings(FragmentMedicationDetailsBinding::bind)
    private val args: MedicationDetailsFragmentArgs by navArgs()
    private val viewModel: MedicationDetailsViewModel by viewModels()
    private lateinit var medicationDetailAdapter: MedicationDetailAdapter
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private val commentsViewModel: CommentsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        if (medicationDetailAdapter.currentList.isEmpty()) {
            viewModel.getMedicationDetails(args.medicationId)
        }
        observeUiState()
        observeCommentsSyncCompletion()
    }

    private fun initUI() {
        setUpRecyclerView()
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
        commentsAdapter = CommentsAdapter { parentEntryId ->
            val action = MedicationDetailsFragmentDirections
                .actionMedicationDetailsFragmentToCommentsFragment(
                    parentEntryId
                )
            findNavController().navigate(action)
        }
        medicationDetailAdapter = MedicationDetailAdapter()
        concatAdapter = ConcatAdapter(medicationDetailAdapter, commentsAdapter)
        val recyclerView = binding.rvMedicationDetailList
        recyclerView.adapter = concatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.progressBar.isVisible = state.onLoading

                    if (state.medicationDetails?.isNotEmpty() == true) {
                        medicationDetailAdapter.submitList(state.medicationDetails)
                        binding.layoutToolbar.topAppBar.title = state.toolbarTitle
                    }

                    handleError(state.onError)

                    viewModel.uiState.value.parentEntryId?.let { commentsViewModel.getComments(it) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                commentsViewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.onLoading

                    if (state.latestComment.isNullOrEmpty().not()) {
                        commentsAdapter.submitList(state.latestComment)
                        // clear comment
                        binding.comment.clearComment()
                    }

                    handleError(state.onError)
                }
            }
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
        val workRequest = WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(SYNC_COMMENTS)
        if (!workRequest.hasObservers()) {
            workRequest.observe(viewLifecycleOwner) {
                if (it.firstOrNull()?.state == WorkInfo.State.SUCCEEDED) {
                    viewModel.uiState.value.parentEntryId?.let { parentEntryId ->
                        commentsViewModel.getComments(
                            parentEntryId
                        )
                    }
                }
            }
        }
    }
}
