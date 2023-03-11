package ca.bc.gov.bchealth.ui.healthrecord.clinicaldocument

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.PdfHelper
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ClinicalDocumentDetailFragment : BaseFragment(null) {
    private val args: ClinicalDocumentDetailFragmentArgs by navArgs()
    private val viewModel: ClinicalDocumentDetailViewModel by viewModels()
    private val pdfDecoderViewModel: PdfDecoderViewModel by viewModels()
    private var fileInMemory: File? = null
    private var resultListener = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { fileInMemory?.delete() }

    override fun getBaseViewModel() = viewModel

    @Composable
    override fun GetComposableLayout() {
        ClinicalDocumentDetailUI(viewModel, ::popNavigation)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.collectOnStart(::handlePdfDownload)
        viewModel.getClinicalDocumentDetails(args.clinicalDocumentId, args.hdid)
        observePdfData()
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

    private fun handlePdfDownload(state: ClinicalDocumentUiState) {
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
