package ca.bc.gov.bchealth.ui.filter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class FilterViewModel : ViewModel() {
    private val _filterState = MutableStateFlow(FilterUiState())
    val filterState: StateFlow<FilterUiState> = _filterState.asStateFlow()

    fun clearFilter() {
        updateFilter(listOf(TimelineTypeFilter.ALL.name), null, null)
    }

    fun updateFilter(search: String) {
        _filterState.update { state ->
            state.copy(search = search)
        }
    }

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
                filterToDate = endDate,
            )
        }
    }

    fun getFilterString(): String = with(filterState.value) {
        var filterString = timelineTypeFilter.joinToString(",")

        if (filterFromDate != null) {
            filterString = filterString.plus(",FROM:").plus(filterFromDate)
        }

        if (filterToDate != null) {
            filterString = filterString.plus(",TO:").plus(filterToDate)
        }

        if (search != null) {
            filterString = filterString.plus(",SEARCH:").plus(search)
        }

        return filterString
    }
}

data class FilterUiState(
    val timelineTypeFilter: List<String> = listOf(TimelineTypeFilter.ALL.name),
    val filterFromDate: String? = null,
    val filterToDate: String? = null,
    val search: String? = null
)
