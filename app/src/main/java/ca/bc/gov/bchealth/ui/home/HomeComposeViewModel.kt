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
                    if (it.appFeatureDto.featureName == null) {
                        QuickAccessTileItem.PredefinedItem(
                            icon = it.appFeatureDto.featureIconId,
                            nameId = it.appFeatureDto.featureNameId ?: -1,
                            destinationId = it.appFeatureDto.destinationId
                        )
                    } else {
                        QuickAccessTileItem.DynamicItem(
                            icon = it.appFeatureDto.featureIconId,
                            nameId = it.appFeatureDto.featureNameId,
                            text = it.appFeatureDto.featureName.orEmpty(),
                            destinationId = it.appFeatureDto.destinationId
                        )
                    }
                }

        _uiState.update { it.copy(quickAccessTileItems = quickAccessTileItems) }
    }
}

data class HomeComposeUiState(
    val quickAccessTileItems: List<QuickAccessTileItem> = emptyList()
)

sealed class QuickAccessTileItem(open val destinationId: Int) {
    data class PredefinedItem(
        val icon: Int,
        val nameId: Int,
        override val destinationId: Int
    ) : QuickAccessTileItem(destinationId)

    data class DynamicItem(
        val icon: Int,
        val nameId: Int?,
        val text: String,
        override val destinationId: Int
    ) : QuickAccessTileItem(destinationId)
}
