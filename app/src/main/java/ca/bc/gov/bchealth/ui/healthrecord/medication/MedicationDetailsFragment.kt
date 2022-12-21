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
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentMedicationDetailsBinding
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.healthrecord.BaseRecordDetailFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicationDetailsFragment : BaseRecordDetailFragment(R.layout.fragment_medication_details) {

    private val binding by viewBindings(FragmentMedicationDetailsBinding::bind)
    private val args: MedicationDetailsFragmentArgs by navArgs()
    private val viewModel: MedicationDetailsViewModel by viewModels()
    private lateinit var medicationDetailAdapter: MedicationDetailAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        if (medicationDetailAdapter.currentList.isEmpty()) {
            viewModel.getMedicationDetails(args.medicationId)
        }
        observeUiState()
        initComments()
    }

    override fun getScrollableView() = binding.rvMedicationDetailList

    override fun getCommentEntryTypeCode() = CommentEntryTypeCode.MEDICATION

    override fun getParentEntryId(): String? = viewModel.uiState.value.parentEntryId

    override fun getCommentView() = binding.comment

    override fun getProgressBar() = binding.progressBar

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
        concatAdapter = ConcatAdapter(medicationDetailAdapter, getRecordCommentsAdapter())

        val recyclerView = binding.rvMedicationDetailList
        recyclerView.adapter = concatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeUiState() {
        viewModel.uiState.collectOnStart { state ->
            binding.progressBar.isVisible = state.onLoading

            if (state.medicationDetails?.isNotEmpty() == true) {
                medicationDetailAdapter.submitList(state.medicationDetails)
                binding.layoutToolbar.topAppBar.title = state.toolbarTitle
            }

            handleError(state.onError)
            getComments(state.parentEntryId)
        }
    }

    override fun handleError(isFailed: Boolean) {
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
}
