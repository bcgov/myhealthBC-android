package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.viewpager2.adapter.FragmentStateAdapter
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentTestResultDetailBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.PdfHelper
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestAndPatientDto
import ca.bc.gov.common.model.test.CovidTestDto
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

/**
 * @author amit metri
 */
@AndroidEntryPoint
class CovidTestResultDetailFragment : BaseFragment(R.layout.fragment_test_result_detail) {

    private val binding by viewBindings(FragmentTestResultDetailBinding::bind)
    private val viewModel: CovidTestResultDetailsViewModel by viewModels()
    private val args: CovidTestResultDetailFragmentArgs by navArgs()
    private val pdfDecoderViewModel: PdfDecoderViewModel by viewModels()
    private var fileInMemory: File? = null
    private var resultListener = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        fileInMemory?.delete()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeDetails()
        observePdfData()
        viewModel.getCovidOrderWithCovidTests(args.covidOrderId)
    }

    private fun observeDetails() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.onLoading
                    state.onCovidTestResultDetail.let { covidTestResult ->
                        if (covidTestResult != null) {
                            initUi(
                                covidTestResult
                            )
                        }
                    }

                    handleServiceDown(state)

                    handlePdfDownload(state)

                    if (state.onError) {
                        showError()
                        viewModel.resetUiState()
                    }

                    handleNoInternetConnection(state)
                }
            }
        }
    }

    private fun handleServiceDown(state: CovidResultDetailUiState) {
        if (!state.isHgServicesUp) {
            binding.root.showServiceDownMessage(requireContext())
            viewModel.resetUiState()
        }
    }

    private fun handleNoInternetConnection(uiState: CovidResultDetailUiState) {
        if (!uiState.isConnected) {
            binding.root.showNoInternetConnectionMessage(requireContext())
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

    private fun handlePdfDownload(state: CovidResultDetailUiState) {
        if (state.pdfData?.isNotEmpty() == true) {
            pdfDecoderViewModel.base64ToPDFFile(state.pdfData)
            viewModel.resetUiState()
        }
    }

    private fun initUi(covidTestResult: CovidOrderWithCovidTestAndPatientDto) {

        val covidTestResultsAdapter = CovidTestResultsAdapter(
            this,
            covidTestResult.covidOrderWithCovidTest.covidTests,
            covidTestResult.covidOrderWithCovidTest.covidOrder.reportAvailable
        )
        binding.viewpagerCovidTestResults.adapter = covidTestResultsAdapter
        if (covidTestResult.covidOrderWithCovidTest.covidTests.size > 1) {
            binding.tabCovidTestResults.visibility = View.VISIBLE
            TabLayoutMediator(
                binding.tabCovidTestResults,
                binding.viewpagerCovidTestResults
            ) { _, _ -> }.attach()
        }

        /*if (covidTestResult.covidOrderWithCovidTest.covidOrder.reportAvailable) {
            with(binding.layoutToolbar.topAppBar) {
                if (willNotDraw()) {
                    setWillNotDraw(false)
                    inflateMenu(R.menu.menu_lab_test_details)
                    setOnMenuItemClickListener { menu ->
                        when (menu.itemId) {
                            R.id.menu_download -> {
                                viewModel.getCovidTestInPdf(args.covidOrderId)
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }*/
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = getString(R.string.covid_19_test_result)
        }
    }

    inner class CovidTestResultsAdapter(
        fragment: Fragment,
        private val covidTests: List<CovidTestDto>,
        private val reportAvailable: Boolean
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = covidTests.size

        override fun createFragment(position: Int): Fragment {

            return CovidTestResultFragment.newInstance(
                args.covidOrderId,
                covidTests[position].id,
                reportAvailable,
                itemClickListener = { viewModel.getCovidTestInPdf(args.covidOrderId) }
            )
        }
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
