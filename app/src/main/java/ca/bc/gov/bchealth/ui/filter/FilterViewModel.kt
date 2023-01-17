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

    fun getFilterString(): String {
        var filterString =
            filterState.value.timelineTypeFilter.joinToString(",")
        if (filterState.value.filterFromDate != null) {
            filterString = filterString.plus(",FROM:")
                .plus(filterState.value.filterFromDate)
        }
        if (filterState.value.filterToDate != null) {
            filterString =
                filterString.plus(",TO:").plus(filterState.value.filterToDate)
        }

        return filterString
    }
}

enum class TimelineTypeFilter {
    ALL,
    MEDICATION,
    LAB_TEST,
    COVID_19_TEST,
    IMMUNIZATION,
    HEALTH_VISIT,
    SPECIAL_AUTHORITY,
    HOSPITAL_VISITS
}

data class FilterUiState(
    val timelineTypeFilter: List<String> = listOf(TimelineTypeFilter.ALL.name),
    val filterFromDate: String? = null,
    val filterToDate: String? = null
)
