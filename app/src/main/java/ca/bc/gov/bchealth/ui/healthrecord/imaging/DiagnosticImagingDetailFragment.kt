package ca.bc.gov.bchealth.ui.healthrecord.imaging

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthToolbar
import ca.bc.gov.bchealth.utils.PdfHelper
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DiagnosticImagingDetailFragment : BaseFragment(null) {
    private val args: DiagnosticImagingDetailFragmentArgs by navArgs()
    private val viewModel: DiagnosticImagingDetailViewModel by viewModels()
    private val pdfDecoderViewModel: PdfDecoderViewModel by viewModels()
    private var fileInMemory: File? = null
    private var resultListener = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { fileInMemory?.delete() }

    override fun getBaseViewModel() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.collectOnStart(::handlePdfDownload)
        viewModel.getDiagnosticImagingDataDetails(args.diagnosticImagingDataId)
        observePdfData()
    }

    @Composable
    override fun GetComposableLayout() {

        val state = viewModel.uiState.collectAsState().value
        MyHealthTheme {

            Scaffold(
                topBar = {
                    MyHealthToolbar(
                        title = state.toolbarTitle
                    ) { findNavController().popBackStack() }
                },
                content = { it ->
                    DiagnosticImagingDetailScreen(
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        viewModel
                    )
                },
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
            )
        }
    }

    private fun observePdfData() {
        pdfDecoderViewModel.uiState.collectOnStart { uiState ->
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

    private fun handlePdfDownload(state: DiagnosticImagingDataDetailUiState) {
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
}
