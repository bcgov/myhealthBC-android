package ca.bc.gov.bchealth.ui.home.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.home.QuickAccessTileItem
import ca.bc.gov.repository.settings.AppFeatureWithQuickAccessTilesRepository
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
    private val appFeatureRepository: AppFeatureWithQuickAccessTilesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickAccessManagementUiState())
    val uiState: StateFlow<QuickAccessManagementUiState> = _uiState.asStateFlow()

    fun loadQuickAccessTileData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val featureWithQuickAccessItems = appFeatureRepository.getManageableAppFeatures()
            .map { QuickAccessTileItem.from(it) }
            .groupBy { it.category }

        _uiState.update {
            it.copy(
                featureWithQuickAccessItems = featureWithQuickAccessItems,
                isLoading = false
            )
        }
    }

    fun toggleItem(item: QuickAccessTileItem) {
        item.isQuickAccess = item.isQuickAccess.not()
    }

    fun saveSelection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            _uiState.value.featureWithQuickAccessItems.forEach {
                it.value.forEach { tile ->
                    // TODO : make call to the query to update the item
                }
            }

            delay(300L)

            _uiState.update { it.copy(isLoading = false, isUpdateCompleted = true) }
        }
    }

    data class QuickAccessManagementUiState(
        val isLoading: Boolean = false,
        val isUpdateCompleted: Boolean = false,
        val featureWithQuickAccessItems: Map<Int, List<QuickAccessTileItem>> = emptyMap()
    )
}
