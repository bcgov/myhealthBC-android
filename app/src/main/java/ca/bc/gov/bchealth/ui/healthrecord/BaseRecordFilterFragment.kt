package ca.bc.gov.bchealth.ui.healthrecord

import android.widget.Filter
import androidx.annotation.LayoutRes
import androidx.core.view.children
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.LayoutChipGroupBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.filter.FilterUiState
import ca.bc.gov.bchealth.ui.filter.FilterViewModel
import ca.bc.gov.bchealth.ui.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.utils.hide
import ca.bc.gov.bchealth.utils.show
import com.google.android.material.chip.Chip

abstract class BaseRecordFilterFragment(@LayoutRes id: Int) : BaseFragment(id) {

    abstract fun getLayoutChipGroup(): LayoutChipGroupBinding
    abstract fun getFilter(): Filter
    abstract fun getFilterViewModel(): FilterViewModel

    fun clearFilterClickListener() {
        getLayoutChipGroup().imgClear.setOnClickListener {
            getFilterViewModel().clearFilter()
            getFilter().filter(getFilterViewModel().getFilterString())
        }
    }

    fun observeFilterState() {
        getFilterViewModel().filterState.collectOnStart { filterState ->

            // update filter date selection
            if (isFilterDateSelected(filterState)) {
                getLayoutChipGroup().chipDate.apply {
                    show()
                    text = when {
                        filterState.filterFromDate.isNullOrBlank() -> {
                            filterState.filterToDate + " " + getString(R.string.before)
                        }
                        filterState.filterToDate.isNullOrBlank() -> {
                            filterState.filterFromDate + " " + getString(R.string.after)
                        }
                        else -> {
                            filterState.filterFromDate + " - " + filterState.filterToDate
                        }
                    }
                }
            } else {
                getLayoutChipGroup().chipDate.hide()
            }

            updateTypeFilterSelection(filterState)

            updateClearButton(filterState)
        }
    }

    private fun updateTypeFilterSelection(filterUiState: FilterUiState) {
        resetTypeFilters()
        filterUiState.timelineTypeFilter.forEach { filterName ->
            TimelineTypeFilter.findByName(filterName)?.let { typeFilter ->
                typeFilter.id?.let {
                    val chip = view?.findViewById<Chip>(it)
                    chip?.show()
                }
            }
        }
    }

    private fun resetTypeFilters() {
        getLayoutChipGroup().cgFilter.children.forEach { chip ->
            if (chip.id != R.id.chip_date) {
                chip.hide()
            }
        }
    }

    private fun updateClearButton(filterState: FilterUiState) {
        if (!isFilterDateSelected(filterState) &&
            filterState.timelineTypeFilter.contains(
                    TimelineTypeFilter.ALL.name
                )
        ) {
            getLayoutChipGroup().imgClear.hide()
        } else {
            getLayoutChipGroup().imgClear.show()
        }
    }

    private fun isFilterDateSelected(filterState: FilterUiState): Boolean {
        if (filterState.filterFromDate.isNullOrBlank() && filterState.filterToDate.isNullOrBlank()) {
            return false
        }
        return true
    }
}