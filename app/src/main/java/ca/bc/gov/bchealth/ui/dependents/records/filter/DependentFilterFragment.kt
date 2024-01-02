package ca.bc.gov.bchealth.ui.dependents.records.filter

import android.os.Bundle
import android.view.View
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import ca.bc.gov.bchealth.ui.filter.FilterFragment
import ca.bc.gov.bchealth.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DependentFilterFragment : FilterFragment() {

    override val filterSharedViewModel: DependentFilterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                filterSharedViewModel.uiState.collect { uiState ->
                    if (uiState.availableFilters.isNotEmpty()) {
                        binding.cgFilterByType.forEach { chip ->
                            chip.toggleVisibility(uiState.availableFilters.contains(chip.id))
                        }
                    }
                }
            }
        }

        filterSharedViewModel.getAvailableFilters()
    }
}
