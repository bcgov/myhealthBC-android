package ca.bc.gov.bchealth.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.common.model.settings.AppFeatureDto
import ca.bc.gov.repository.settings.AppFeatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeComposeViewModel @Inject constructor(
    private val appFeatureRepository: AppFeatureRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeComposeUiState())
    val uiState: StateFlow<HomeComposeUiState> = _uiState.asStateFlow()

    fun loadQuickAccessTiles() = viewModelScope.launch {
        val quickAccessTileItems = appFeatureRepository.getQuickAccessTiles().map {
            it.toUiItem()
        }

        _uiState.update { it.copy(quickAccessTileItems = quickAccessTileItems) }
    }
}

data class HomeComposeUiState(
    val quickAccessTileItems: List<QuickAccessTileItem> = emptyList()
)

sealed class QuickAccessTileItem(
    open val id: Long,
    open val destinationId: Int,
    open val destinationParam: String?,
    open val categoryId: Int,
    open var enabled: Boolean,
) {
    data class PredefinedItem(
        override val id: Long,
        val icon: Int,
        val nameId: Int,
        override val destinationId: Int,
        override val destinationParam: String?,
        override val categoryId: Int,
        override var enabled: Boolean,
    ) : QuickAccessTileItem(id, destinationId, destinationParam, categoryId, enabled)

    data class DynamicItem(
        override val id: Long,
        val icon: Int,
        val nameId: Int?,
        val text: String,
        override val destinationId: Int,
        override val destinationParam: String?,
        override val categoryId: Int,
        override var enabled: Boolean,
    ) : QuickAccessTileItem(id, destinationId, destinationParam, categoryId, enabled)
}

fun AppFeatureDto.toUiItem(): QuickAccessTileItem =
    if (this.featureName == null) {
        QuickAccessTileItem.PredefinedItem(
            id = this.id,
            icon = this.featureIconId,
            nameId = this.featureNameId ?: -1,
            destinationId = this.destinationId,
            destinationParam = this.destinationParam,
            categoryId = this.categoryId,
            enabled = this.isQuickAccessEnabled
        )
    } else {
        QuickAccessTileItem.DynamicItem(
            id = this.id,
            icon = this.featureIconId,
            nameId = this.featureNameId,
            text = this.featureName.orEmpty(),
            destinationId = this.destinationId,
            destinationParam = this.destinationParam,
            categoryId = this.categoryId,
            enabled = this.isQuickAccessEnabled
        )
    }
