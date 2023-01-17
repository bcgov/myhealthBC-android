package ca.bc.gov.bchealth.ui.healthrecord.hospitalvisits

import android.os.Bundle
import android.view.View
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentHospitalVisitDetailBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HospitalVisitDetailFragment : BaseFragment(R.layout.fragment_hospital_visit_detail) {
    private val binding by viewBindings(FragmentHospitalVisitDetailBinding::bind)
    private val args: HospitalVisitDetailFragmentArgs by navArgs()
    private val viewModel: HospitalVisitDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.collectOnStart(::updateUi)
        viewModel.getHospitalVisitDetails(args.hospitalVisitId)
    }

    private fun updateUi(uiState: HospitalVisitUiState) {
        binding.layoutToolbar.topAppBar.title = uiState.toolbarTitle

        binding.composeBody.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    HospitalVisitDetailUI(uiState.uiList)
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
