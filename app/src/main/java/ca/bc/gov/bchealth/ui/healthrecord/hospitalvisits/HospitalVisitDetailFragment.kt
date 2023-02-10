package ca.bc.gov.bchealth.ui.healthrecord.hospitalvisits

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.databinding.FragmentHospitalVisitDetailBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthToolbar
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
        binding.composeBody.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyHealthTheme {
                    Scaffold(topBar = {
                        MyHealthToolbar(title = uiState.toolbarTitle.orEmpty()) {
                            findNavController().popBackStack()
                        }
                    }) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .statusBarsPadding()
                                .navigationBarsPadding()
                                .padding(innerPadding)
                        ) {
                            HospitalVisitDetailUI(uiState.uiList)
                        }
                    }
                }
            }
        }
    }
}
