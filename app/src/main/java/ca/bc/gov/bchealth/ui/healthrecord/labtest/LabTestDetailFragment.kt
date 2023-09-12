package ca.bc.gov.bchealth.ui.healthrecord.labtest

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.comment.CommentEntryTypeCode
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsUiState
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.custom.MyHealthScaffold
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.PdfHelper
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import ca.bc.gov.common.BuildConfig
import ca.bc.gov.repository.SYNC_COMMENTS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class LabTestDetailFragment : BaseFragment(null) {
    private val viewModel: LabTestDetailViewModel by viewModels()
    private val commentsViewModel: CommentsViewModel by viewModels()

    private val args: LabTestDetailFragmentArgs by navArgs()
    private val pdfDecoderViewModel: PdfDecoderViewModel by viewModels()
    private var fileInMemory: File? = null
    private var resultListener = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { fileInMemory?.delete() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePdfData()
        observeCommentsSyncCompletion()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getLabTestDetails(args.labOrderId)
    }

    @Composable
    override fun GetComposableLayout() {

        val uiState = viewModel.uiState.collectAsState().value

        var commentState: CommentsUiState? = null

        if (BuildConfig.FLAG_ADD_COMMENTS) {
            uiState.parentEntryId?.let { commentsViewModel.getComments(it) }
            commentState = commentsViewModel.uiState.collectAsState().value
        }

        MyHealthScaffold(
            title = uiState.toolbarTitle,
            isLoading = uiState.onLoading,
            navigationAction = { findNavController().popBackStack() }
        ) {
            LabTestScreen(
                uiState = uiState,
                onClickViewPdf = { viewModel.getLabTestPdf(args.hdid) },
                onClickFaq = { context?.redirect(getString(R.string.faq_link)) },
                onClickComments = ::navigateToComments,
                commentsSummary = commentState?.commentsSummary,
                onSubmitComment = ::onSubmitComment,
            )
        }
        handledServiceDown(uiState)

        if (uiState.onError) {
            showError()
            viewModel.resetUiState()
        }

        handlePdfDownload(uiState)

        handleNoInternetConnection(uiState)

        if (commentState?.isBcscSessionActive == false) findNavController().popBackStack()
    }

    private fun observeCommentsSyncCompletion() {
        if (BuildConfig.FLAG_ADD_COMMENTS.not()) return

        observeWork(SYNC_COMMENTS) {
            if (it == WorkInfo.State.SUCCEEDED) {
                getParentEntryId()?.let { parentId ->
                    commentsViewModel.getComments(parentId)
                }
            }
        }
    }

    private fun onSubmitComment(content: String) {
        getParentEntryId()?.let { parentEntryId ->
            commentsViewModel.addComment(
                parentEntryId,
                content,
                CommentEntryTypeCode.LAB_RESULTS.value,
            )
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

    private fun getParentEntryId(): String? = viewModel.uiState.value.parentEntryId

    private fun handledServiceDown(state: LabTestDetailUiState) {
        if (!state.isHgServicesUp) {
            showServiceDownMessage()
            viewModel.resetUiState()
        }
    }

    private fun handleNoInternetConnection(uiState: LabTestDetailUiState) {
        if (!uiState.isConnected) {
            showNoInternetConnectionMessage()
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
