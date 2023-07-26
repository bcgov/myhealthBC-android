package ca.bc.gov.bchealth.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoveQuickAccessTileBottomSheetFragment : BottomSheetDialogFragment() {

    val args: RemoveQuickAccessTileBottomSheetFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HealthGatewayTheme {
                    RemoveQuickAccessTileBottomSheetScreen(name = args.name)
                }
            }
        }
    }
}
