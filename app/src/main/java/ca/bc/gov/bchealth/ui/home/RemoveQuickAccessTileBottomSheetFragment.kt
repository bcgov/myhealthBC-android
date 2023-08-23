package ca.bc.gov.bchealth.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoveQuickAccessTileBottomSheetFragment : BottomSheetDialogFragment() {

    private val removeQuickAccessTileViewModel: RemoveQuickAccessTileViewModel by viewModels()
    private val args: RemoveQuickAccessTileBottomSheetFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HealthGatewayTheme {
                    RemoveQuickAccessTileBottomSheetScreen(
                        viewModel = removeQuickAccessTileViewModel,
                        id = args.id,
                        name = args.name,
                        onRemoveClicked = ::onRemoveClicked,
                        ondDismissClicked = ::ondDismissClicked
                    )
                }
            }
        }
    }

    private fun onRemoveClicked() {
        dismiss()
        findNavController().navigate(R.id.action_home_self)
    }
    private fun ondDismissClicked() {
        dismiss()
    }
}
