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

    fun updateFilterTypes(timelineTypeFilter: List<TimelineTypeFilter>) {
        _filterState.update { state ->
            state.copy(
                timelineTypeFilter = timelineTypeFilter
            )
        }
    }

    fun updateFilterDates(fromDate: String?, toDate: String?) {
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
                filterFromDate = startDate,
                filterToDate = endDate
            )
        }
    }
}

enum class TimelineTypeFilter {
    ALL,
    NONE,
    MEDICATION,
    LAB_TEST,
    COVID_19_TEST,
    IMMUNIZATION
}

data class FilterUiState(
    val timelineTypeFilter: List<TimelineTypeFilter> = listOf(TimelineTypeFilter.ALL),
    val filterFromDate: String? = null,
    val filterToDate: String? = null
)
