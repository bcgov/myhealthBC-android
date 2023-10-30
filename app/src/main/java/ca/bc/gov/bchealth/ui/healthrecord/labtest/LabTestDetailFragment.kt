package ca.bc.gov.bchealth.ui.healthrecord.labtest

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentLabTestDetailBinding
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.healthrecord.BaseRecordDetailFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.PdfHelper
import ca.bc.gov.bchealth.utils.launchAndRepeatWithLifecycle
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import ca.bc.gov.bchealth.widget.AddCommentLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class LabTestDetailFragment : BaseRecordDetailFragment(R.layout.fragment_lab_test_detail) {

    private val binding by viewBindings(FragmentLabTestDetailBinding::bind)
    private val viewModel: LabTestDetailViewModel by viewModels()

    private lateinit var labTestDetailAdapter: LabTestDetailAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private val args: LabTestDetailFragmentArgs by navArgs()
    private val pdfDecoderViewModel: PdfDecoderViewModel by viewModels()
    private var fileInMemory: File? = null
    private var resultListener = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { fileInMemory?.delete() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupComposeToolbar(binding.composeToolbar.root)
        setUpRecyclerView(args.hdid)
        viewModel.getLabTestDetails(args.labOrderId)
        observeUiState()
        observePdfData()
        initComments()
    }

    override fun getCommentView(): AddCommentLayout = binding.comment

    override fun getScrollableView() = binding.rvLabTestDetailList

    override fun getCommentEntryTypeCode() = CommentEntryTypeCode.LAB_RESULTS

    override fun getParentEntryId(): String? = viewModel.uiState.value.parentEntryId

    override fun getProgressBar(): View = binding.progressBar

    private fun setUpRecyclerView(hdid: String?) {
        labTestDetailAdapter = LabTestDetailAdapter(
            viewPdfClickListener = { viewModel.getLabTestPdf(hdid) }
        )

        concatAdapter = ConcatAdapter(labTestDetailAdapter, getRecordCommentsAdapter())

        binding.rvLabTestDetailList.adapter = concatAdapter
    }

    private fun observeUiState() {
        launchAndRepeatWithLifecycle {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.onLoading

                handledServiceDown(state)

                if (state.labTestDetails?.isNotEmpty() == true) {
                    labTestDetailAdapter.submitList(state.labTestDetails)
                    setupComposeToolbar(binding.composeToolbar.root, state.toolbarTitle)
                }

                if (state.onError) {
                    showError()
                    viewModel.resetUiState()
                }

                handlePdfDownload(state)

                handleNoInternetConnection(state)
                getComments(state.parentEntryId)
            }
        }
    }

    private fun handledServiceDown(state: LabTestDetailUiState) {
        if (!state.isHgServicesUp) {
            binding.root.showServiceDownMessage(requireContext())
            viewModel.resetUiState()
        }
    }

    private fun handleNoInternetConnection(uiState: LabTestDetailUiState) {
        if (!uiState.isConnected) {
            binding.root.showNoInternetConnectionMessage(requireContext())
            viewModel.resetUiState()
        }
    }

    private fun handlePdfDownload(state: LabTestDetailUiState) {
        if (state.pdfData?.isNotEmpty() == true) {
            pdfDecoderViewModel.base64ToPDFFile(state.pdfData)
            viewModel.resetUiState()
        }
    }

    private fun showError() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = getString(R.string.error),
            msg = getString(R.string.error_message),
            positiveBtnMsg = getString(R.string.dialog_button_ok)
        )
    }

    private fun observePdfData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pdfDecoderViewModel.uiState.collect { uiState ->
                    uiState.pdf?.let {
                        val (base64Pdf, file) = it
                        if (file != null) {
                            try {
                                fileInMemory = file
                                PdfHelper().showPDF(file, requireActivity(), resultListener)
                            } catch (e: Exception) {
                                fallBackToPdfRenderer(base64Pdf)
                            }
                        } else {
                            fallBackToPdfRenderer(base64Pdf)
                        }
                        pdfDecoderViewModel.resetUiState()
                    }
                }
            }
        }
    }

    private fun fallBackToPdfRenderer(federalTravelPass: String) {
        findNavController().navigate(
            R.id.pdfRendererFragment,
            bundleOf(
                "base64pdf" to federalTravelPass,
                "title" to getString(R.string.lab_test)
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fileInMemory != null) {
            fileInMemory?.delete()
            fileInMemory = null
        }
    }
}
