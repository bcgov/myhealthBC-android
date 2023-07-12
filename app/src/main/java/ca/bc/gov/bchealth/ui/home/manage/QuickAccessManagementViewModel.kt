package ca.bc.gov.bchealth.ui.home.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.bc.gov.bchealth.ui.home.QuickAccessTileItem
import ca.bc.gov.bchealth.ui.home.toUiItem
import ca.bc.gov.repository.settings.AppFeatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _uiState = MutableStateFlow(mapOf<Int, List<QuickAccessTileItem>>())
    val uiState: StateFlow<Map<Int, List<QuickAccessTileItem>>> = _uiState.asStateFlow()

    fun loadUiList() = viewModelScope.launch {
        val manageableTiles = appFeatureRepository.getManageableTiles()
            .map { it.toUiItem() }
            .groupBy { it.categoryId }

        _uiState.update { manageableTiles }
    }

    fun toggleItem(item: QuickAccessTileItem) {
        item.enabled = item.enabled.not()
    }

    fun saveSelection() {
        // todo
        val result = _uiState.value
        println(result)
    }
}
