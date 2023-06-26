package ca.bc.gov.bchealth.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.repository.settings.AppFeatureWithQuickAccessTilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeComposeViewModel @Inject constructor(
    private val appFeatureWithQuickAccessTilesRepository: AppFeatureWithQuickAccessTilesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeComposeUiState())
    val uiState: StateFlow<HomeComposeUiState> = _uiState.asStateFlow()

    fun loadQuickAccessTiles() = viewModelScope.launch {
        val quickAccessTileItems =
            appFeatureWithQuickAccessTilesRepository.getAppFeaturesWithQuickAccessTiles()
                .filter { it.appFeatureDto.isQuickAccessEnabled }
                .map {
                    QuickAccessTileItem(
                        it.appFeatureDto.featureIconId,
                        it.appFeatureDto.featureNameId,
                        it.appFeatureDto.destinationId
                    )
                }

        _uiState.update { it.copy(quickAccessTileItems = quickAccessTileItems) }
    }
}

data class HomeComposeUiState(
    val quickAccessTileItems: List<QuickAccessTileItem> = emptyList()
)

data class QuickAccessTileItem(
    val icon: Int,
    val name: Int,
    val destinationId: Int
)
