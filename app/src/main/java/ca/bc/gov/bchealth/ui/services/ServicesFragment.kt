package ca.bc.gov.bchealth.ui.services

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.BaseViewModel
import ca.bc.gov.bchealth.ui.NavigationAction
import ca.bc.gov.bchealth.ui.custom.MyHealthToolBar
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.PdfHelper
import ca.bc.gov.bchealth.utils.launchAndRepeatWithLifecycle
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showErrorSnackbar
import ca.bc.gov.bchealth.viewmodel.PdfDecoderViewModel
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class ServicesFragment : BaseSecureFragment(null) {

    private val pdfDecoderViewModel: PdfDecoderViewModel by viewModels()
    private val servicesViewModel: ServicesViewModel by viewModels()
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private var fileInMemory: File? = null
    private var resultListener = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        fileInMemory?.delete()
    }

    override fun getBaseViewModel(): BaseViewModel {
        return servicesViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchAndRepeatWithLifecycle {
            handlePDFState()
        }

        launchAndRepeatWithLifecycle(Lifecycle.State.RESUMED) {
            bcscAuthViewModel.authStatus.collect {
                if (it.showLoading) {
                    servicesViewModel.showProgressBar()
                } else {
                    it.loginStatus?.let { status ->
                        when (status) {
                            LoginStatus.ACTIVE -> {
                                observeHealthRecordsSyncCompletion()
                            }

                            LoginStatus.EXPIRED -> {
                                findNavController().navigate(R.id.bcServiceCardSessionFragment)
                            }

                            LoginStatus.NOT_AUTHENTICATED -> {
                                findNavController().navigate(R.id.bcServicesCardLoginFragment)
                            }
                        }
                    }
                }
            }
        }
        bcscAuthViewModel.checkSession()
    }

    @Composable
    override fun GetComposableLayout() {
        MyHealthTheme {
            Scaffold(
                topBar = {
                    MyHealthToolBar(
                        title = "",
                        actions = {
                            IconButton(onClick = { findNavController().navigate(R.id.settingsFragment) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_settings),
                                    contentDescription = stringResource(
                                        id = R.string.settings
                                    ),
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        }
                    )
                },
                content = { it ->
                    ServicesScreen(
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        viewModel = servicesViewModel,
                        onRegisterOnUpdateDecisionClicked = { url ->
                            requireActivity().redirect(url)
                        },
                        onDownloadButtonClicked = { fileId ->
                            servicesViewModel.getPatientFile(fileId)
                        },
                        openPdfFile = { pdf ->
                            pdf?.let { file ->
                                pdfDecoderViewModel.base64ToPDFFile(file)
                            }
                        },
                        onError = ::displayError
                    )
                },
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
            )
        }
    }

    private fun observeHealthRecordsSyncCompletion() {
        observeWork(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME) { state ->
            if (state == WorkInfo.State.RUNNING) {
                servicesViewModel.showProgressBar()
            } else {
                servicesViewModel.getOrganDonationStatus()
            }
        }
    }

    private suspend fun handlePDFState() {
        pdfDecoderViewModel.uiState.collect { uiState ->
            if (uiState.pdf != null) {
                val (federalTravelPass, file) = uiState.pdf
                if (file != null) {
                    try {
                        fileInMemory = file
                        PdfHelper().showPDF(file, requireActivity(), resultListener)
                    } catch (e: Exception) {
                        navigateToViewOrganDonorDecision(federalTravelPass)
                    }
                } else {
                    navigateToViewOrganDonorDecision(federalTravelPass)
                }
                pdfDecoderViewModel.resetUiState()
            }
        }
    }

    private fun navigateToViewOrganDonorDecision(organDonorDecisionFile: String) {
        findNavController().navigate(
            R.id.pdfRendererFragment,
            bundleOf(
                "base64pdf" to organDonorDecisionFile,
                "title" to getString(R.string.organ_donor_registration_title)
            )
        )
    }

    override fun handleNavigationAction(navigationAction: NavigationAction) {
        when (navigationAction) {
            NavigationAction.ACTION_BACK -> {
                findNavController().popBackStack()
            }

            NavigationAction.ACTION_RE_CHECK -> {
                bcscAuthViewModel.checkSession()
            }
        }
    }

    private fun displayError() {
        view.showErrorSnackbar(getString(R.string.error_message))
    }
}
