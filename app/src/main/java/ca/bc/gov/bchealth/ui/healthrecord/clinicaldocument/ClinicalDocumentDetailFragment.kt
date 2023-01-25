package ca.bc.gov.bchealth.ui.healthrecord.clinicaldocument

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.databinding.FragmentClinicalDocumentDetailBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClinicalDocumentDetailFragment : BaseFragment(R.layout.fragment_hospital_visit_detail) {
    private val binding by viewBindings(FragmentClinicalDocumentDetailBinding::bind)
    private val args: ClinicalDocumentDetailFragmentArgs by navArgs()
    private val viewModel: ClinicalDocumentDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.collectOnStart(::updateUi)
        viewModel.getClinicalDocumentDetails(args.clinicalDocumentId)
    }

    private fun updateUi(uiState: ClinicalDocumentUiState) {
        binding.layoutToolbar.topAppBar.title = uiState.toolbarTitle

        if (uiState.uiList.isEmpty()) return

        binding.composeBody.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyHealthTheme {
                    ClinicalDocumentDetailUI(uiState.uiList) {
                        viewModel.onClickDownload()
                    }
                }
            }
        }
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}
