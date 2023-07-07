package ca.bc.gov.bchealth.ui.home.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuickAccessManagementViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuickAccessManagementUiState())
    val uiState: StateFlow<QuickAccessManagementUiState> = _uiState.asStateFlow()

    fun loadUiList() = viewModelScope.launch {
        _uiState.update {
            QuickAccessManagementUiState(
                listOf(
                    QuickAccessManagementList(
                        "Health record",
                        listOf(
                            QuickAccessManagementItem("My Notes", false),
                            QuickAccessManagementItem("Immunization", true),
                            QuickAccessManagementItem("Medications", false),
                            QuickAccessManagementItem("Lab Results", false),
                            QuickAccessManagementItem("Special authority", false),
                            QuickAccessManagementItem("Health visit", false),
                            QuickAccessManagementItem("Clinic documents", false),
                        )
                    ),
                    QuickAccessManagementList(
                        "Service",
                        listOf(
                            QuickAccessManagementItem("Organ donor", false),
                        )
                    ),
                    QuickAccessManagementList(
                        "Dependents’ records",
                        listOf(
                            QuickAccessManagementItem("Jane", false),
                            QuickAccessManagementItem("Anne", false),
                        )
                    )
                )
            )
        }
    }

    fun toggleItem(item: QuickAccessManagementItem) {
        item.selected = item.selected.not()
    }

    fun saveSelection() {
        // todo
        val result = _uiState.value.uiList
        println(result)
    }

    data class QuickAccessManagementUiState(
        val uiList: List<QuickAccessManagementList> = listOf()
    )

    data class QuickAccessManagementList(
        val tileCategory: String,
        val tiles: List<QuickAccessManagementItem>
    )

    data class QuickAccessManagementItem(
        val tileName: String,
        var selected: Boolean
    )
}
