package ca.bc.gov.bchealth.ui.healthrecord.labtest

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentLabTestDetailBinding
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.viewBindings
import kotlinx.coroutines.launch

class LabTestDetailFragment : Fragment(R.layout.fragment_lab_test_detail) {

    private val binding by viewBindings(FragmentLabTestDetailBinding::bind)
    private val viewModel: LabTestDetailViewModel by viewModels()
    private lateinit var labTestDetailAdapter: LabTestDetailAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        viewModel.getLabTestDetails()
        observeUiState()
    }

    private fun initUI() {
        setToolBar()
        setUpRecyclerView()
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
        labTestDetailAdapter = LabTestDetailAdapter()
        binding.rvLabTestDetailList.apply {
            adapter = labTestDetailAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.progressBar.isVisible = state.onLoading

                    if (state.labTestDetails?.isNotEmpty() == true) {
                        labTestDetailAdapter.submitList(state.labTestDetails)
                        binding.toolbar.tvTitle.text = state.toolbarTitle
                    }

                    if (state.onError) {
                        showError()
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
}