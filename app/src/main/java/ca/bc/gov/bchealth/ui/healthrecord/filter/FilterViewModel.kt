package ca.bc.gov.bchealth.ui.healthrecord.filter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor() : ViewModel() {
    private val _filterState = MutableStateFlow(FilterUiState())
    val filterState: StateFlow<FilterUiState> = _filterState.asStateFlow()

    fun updateFilter(timelineTypeFilter: List<String>, fromDate: String?, toDate: String?) {
        val startDate: String? = if (fromDate.isNullOrBlank()) {
            null
        } else {
            fromDate
        }
        val endDate: String? = if (toDate.isNullOrBlank()) {
            null
        } else {
            toDate
        }

        _filterState.update { state ->
            state.copy(
                timelineTypeFilter = timelineTypeFilter,
                filterFromDate = startDate,
                filterToDate = endDate
            )
        }
    }

    fun addFilterType(type: String) {
        val list = _filterState.value.timelineTypeFilter.toMutableList()
        list.add(type)
        _filterState.update { state ->
            state.copy(
                timelineTypeFilter = list,
                filterFromDate = _filterState.value.filterFromDate,
                filterToDate = _filterState.value.filterToDate
            )
        }
    }

    fun removeFilterType(type: String) {
        val list = _filterState.value.timelineTypeFilter.toMutableList()
        list.remove(type)
        _filterState.update { state ->
            state.copy(
                timelineTypeFilter = list,
                filterFromDate = _filterState.value.filterFromDate,
                filterToDate = _filterState.value.filterToDate
            )
        }
    }
}

enum class TimelineTypeFilter {
    ALL,
    MEDICATION,
    LAB_TEST,
    COVID_19_TEST,
    IMMUNIZATION,
    PROTECTIVE_WORD
}

data class FilterUiState(
    val timelineTypeFilter: List<String> = listOf(TimelineTypeFilter.ALL.name),
    val filterFromDate: String? = null,
    val filterToDate: String? = null
)
