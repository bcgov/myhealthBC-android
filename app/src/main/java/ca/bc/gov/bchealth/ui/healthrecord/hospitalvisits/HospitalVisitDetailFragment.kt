package ca.bc.gov.bchealth.ui.healthrecord.hospitalvisits

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HospitalVisitDetailFragment : BaseFragment(null) {
    private val args: HospitalVisitDetailFragmentArgs by navArgs()
    private val viewModel: HospitalVisitDetailViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        HospitalVisitDetailUI(viewModel, { findNavController().popBackStack() })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getHospitalVisitDetails(args.hospitalVisitId)
    }
}
