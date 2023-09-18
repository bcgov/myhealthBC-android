package ca.bc.gov.bchealth.ui.healthrecord.labtest

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.utils.PdfHelper
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.viewmodel.PdfDecoderUiState
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import dagger.hilt.android.AndroidEntryPoint
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

    @Composable
    override fun GetComposableLayout() {
        LabTestScreen(
            hdid = args.hdid,
            labOrderId = args.labOrderId,
            viewModel = viewModel,
            commentsViewModel = commentsViewModel,
            pdfDecoderViewModel = pdfDecoderViewModel,
            onClickFaq = { context?.redirect(getString(R.string.faq_link)) },
            onPopNavigation = findNavController()::popBackStack,
            showServiceDownMessage = ::showServiceDownMessage,
            showNoInternetConnectionMessage = ::showNoInternetConnectionMessage,
            onPdfStateChanged = ::onPdfStateChanged,
            navigateToComments = ::navigateToComments
        )
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

    private fun onPdfStateChanged(uiState: PdfDecoderUiState) {
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
