package ca.bc.gov.bchealth.ui.healthrecord.hospitalvisits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HospitalVisitDetailFragment : Fragment() {
    private val args: HospitalVisitDetailFragmentArgs by navArgs()
    private val viewModel: HospitalVisitDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            HospitalVisitDetailUI(viewModel, ::popNavigation)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getHospitalVisitDetails(args.hospitalVisitId)
    }

    private fun popNavigation() {
        findNavController().popBackStack()
    }
}
