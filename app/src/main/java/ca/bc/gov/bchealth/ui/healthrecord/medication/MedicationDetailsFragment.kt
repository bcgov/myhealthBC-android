package ca.bc.gov.bchealth.ui.healthrecord.medication

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMedicationDetailsBinding
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.updateCommentEndIcon
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MedicationDetailsFragment : Fragment(R.layout.fragment_medication_details) {

    private val binding by viewBindings(FragmentMedicationDetailsBinding::bind)
    private val args: MedicationDetailsFragmentArgs by navArgs()
    private val viewModel: MedicationDetailsViewModel by viewModels()
    private lateinit var medicationDetailAdapter: MedicationDetailAdapter
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        if (medicationDetailAdapter.currentList.isEmpty()) {
            viewModel.getMedicationDetails(args.medicationId)
        }
        observeUiState()
    }

    private fun initUI() {
        setToolBar()
        setUpRecyclerView()
        addCommentListener()
    }

    private fun setToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE

            line1.visibility = View.VISIBLE
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
                        binding.toolbar.tvTitle.text = state.toolbarTitle
                    }

                    if (state.comments.isNotEmpty()) {
                        commentsAdapter.submitList(state.comments)
                        // clear comment
                        binding.comment.edComment.setText("")
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
        binding.comment.tipComment.apply {
            updateCommentEndIcon(requireContext())
            setEndIconOnClickListener {
                if (!binding.comment.edComment.text.isNullOrBlank()) {
                    viewModel.addComment(
                        binding.comment.edComment.text.toString(),
                        CommentEntryTypeCode.MEDICATION.value
                    )
                }
            }
        }
    }
}
