package ca.bc.gov.bchealth.ui.home.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.home.QuickAccessTileItem
import ca.bc.gov.bchealth.ui.home.toUiItem
import ca.bc.gov.repository.settings.AppFeatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickAccessManagementViewModel @Inject constructor(
    private val appFeatureRepository: AppFeatureRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickAccessManagementUiState())
    val uiState: StateFlow<QuickAccessManagementUiState> = _uiState.asStateFlow()

    fun loadTilesUi() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val tiles = appFeatureRepository.getManageableTiles()
            .map { it.toUiItem() }
            .groupBy { it.categoryId }

        _uiState.update { it.copy(uiMap = tiles, isLoading = false) }
    }

    fun toggleItem(item: QuickAccessTileItem) {
        item.enabled = item.enabled.not()
    }

    fun saveSelection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            for ((_, features) in _uiState.value.uiMap) {
                features.forEach { tileItem ->
                    appFeatureRepository.updateQuickAccessFlag(tileItem.id, tileItem.enabled)
                }
            }

            delay(300L)

            _uiState.update { it.copy(isLoading = false, isUpdateCompleted = true) }
        }
    }

    data class QuickAccessManagementUiState(
        val uiMap: Map<Int, List<QuickAccessTileItem>> = mapOf(),
        val isLoading: Boolean = false,
        val isUpdateCompleted: Boolean = false,
    )
}
