package ca.bc.gov.bchealth.ui.healthrecord.cancer

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.custom.MyHealthToolbar
import ca.bc.gov.bchealth.utils.PdfHelper
import ca.bc.gov.bchealth.utils.launchAndRepeatWithLifecycle
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * @author pinakin.kansara
 * Created 2024-01-19 at 10:56 a.m.
 */
@AndroidEntryPoint
class BcCancerScreeningDetailFragment : BaseFragment(null) {
    private val args: BcCancerScreeningDetailFragmentArgs by navArgs()
    private val viewModel: BcCancerScreeningDetailViewModel by viewModels()
    private val pdfDecoderViewModel: PdfDecoderViewModel by viewModels()
    private val commentsViewModel: CommentsViewModel by viewModels()
    private var fileInMemory: File? = null
    private var resultListener = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { fileInMemory?.delete() }

    override fun getBaseViewModel() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchAndRepeatWithLifecycle {
            viewModel.uiState.collect {
                handlePdfDownload(it)
            }
        }
        observePdfData()
    }

    @Composable
    override fun GetComposableLayout() {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        HealthGatewayTheme {
            Scaffold(
                topBar = {
                    MyHealthToolbar(
                        title = uiState.toolbarTitle
                    ) { findNavController().popBackStack() }
                },
                content = { it ->
                    BcCancerScreeningDetailScreen(
                        onClickComments = ::onClickComments,
                        onClickLink = ::onClickLink,
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        viewModel,
                        commentsViewModel,
                        args.bcCancerScreeningDataId
                    )
                },
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
            )
        }
    }

    private fun observePdfData() {
        launchAndRepeatWithLifecycle {
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

    private fun handlePdfDownload(state: BcCancerScreeningDataDetailUiState) {
        if (state.pdfData?.isNotEmpty() == true) {
            pdfDecoderViewModel.base64ToPDFFile(state.pdfData)
            viewModel.resetPdfState()
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

    private fun onClickComments(commentsSummary: CommentsSummary) {
        findNavController().navigate(
            R.id.commentsFragment,
            bundleOf(
                "parentEntryId" to commentsSummary.parentEntryId,
                "recordType" to commentsSummary.entryTypeCode,
            )
        )
    }

    private fun onClickLink(url: String) {
        requireContext().redirect(url)
    }
}
