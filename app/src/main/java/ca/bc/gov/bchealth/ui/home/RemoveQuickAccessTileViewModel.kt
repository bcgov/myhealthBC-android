package ca.bc.gov.bchealth.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.QuickAccessTileShowAsQuickLinkDto
import ca.bc.gov.repository.settings.QuickAccessTileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoveQuickAccessTileViewModel @Inject constructor(
    private val quickAccessTileRepository: QuickAccessTileRepository
) : ViewModel() {

    fun updateTile(id: Long) = viewModelScope.launch {
        quickAccessTileRepository.update(QuickAccessTileShowAsQuickLinkDto(id, showAsQuickAccess = false))
    }
}
