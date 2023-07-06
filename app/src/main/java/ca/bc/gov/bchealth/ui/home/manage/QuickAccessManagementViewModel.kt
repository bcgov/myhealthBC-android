package ca.bc.gov.bchealth.ui.home.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

        val featureWithQuickAccessItems = appFeatureRepository.getAppFeaturesWithQuickAccessTiles().filter { it.appFeatureDto.hasManageableQuickAccessLinks }
            .map {
                FeatureWithQuickAccessItems(
                    id = it.appFeatureDto.id,
                    name = it.appFeatureDto.name.value,
                    quickAccessItems = it.quickAccessTiles.map { tile ->
                        QuickAccessItem(
                            tile.id,
                            tile.tileName.value,
                            tile.showAsQuickAccess
                        )
                    }
                )
            }

        _uiState.update { it.copy(featureWithQuickAccessItems = featureWithQuickAccessItems, isLoading = false) }
    }

    fun toggleItem(item: QuickAccessItem) {
        item.isEnabled = item.isEnabled.not()
    }

    fun saveSelection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            _uiState.value.featureWithQuickAccessItems.forEach {
                it.quickAccessItems.forEach { tile ->
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
        val featureWithQuickAccessItems: List<FeatureWithQuickAccessItems> = emptyList()
    )

    data class FeatureWithQuickAccessItems(
        val id: Long = 0,
        val name: String,
        val quickAccessItems: List<QuickAccessItem> = emptyList()
    )

    data class QuickAccessItem(
        val id: Long = 0,
        val name: String,
        var isEnabled: Boolean = false
    )
}
