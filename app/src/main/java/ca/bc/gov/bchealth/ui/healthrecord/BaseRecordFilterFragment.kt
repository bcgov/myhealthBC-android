package ca.bc.gov.bchealth.ui.healthrecord

import android.view.View
import android.widget.Filter
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.IncludeSearchBarBinding
import ca.bc.gov.bchealth.databinding.LayoutChipGroupBinding
import ca.bc.gov.bchealth.ui.BaseSecureFragment
import ca.bc.gov.bchealth.ui.filter.FilterUiState
import ca.bc.gov.bchealth.ui.filter.FilterViewModel
import ca.bc.gov.bchealth.ui.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.utils.hide
import ca.bc.gov.bchealth.utils.launchAndRepeatWithLifecycle
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.common.BuildConfig
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseRecordFilterFragment(@LayoutRes id: Int) : BaseSecureFragment(id) {
    private var queryTextChangedJob: Job? = null

    abstract fun getSearchBar(): IncludeSearchBarBinding
    abstract fun getLayoutChipGroup(): LayoutChipGroupBinding
    abstract fun getFilter(): Filter
    abstract fun getFilterViewModel(): FilterViewModel

    @IdRes
    abstract fun getFilterFragmentId(): Int

    fun setupSearchView() {
        if (BuildConfig.FLAG_SEARCH_RECORDS.not()) return

        getSearchBar().apply {

            ivFilter.setOnClickListener {
                it.requestFocus()
                findNavController().navigate(getFilterFragmentId())
            }

            searchRecords.apply {
                findViewById<View>(R.id.search_close_btn)?.setOnClickListener {
                    setQuery("", false)
                    clearFocus()
                }
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(query: String): Boolean {
                        onChangeQuery(query)
                        return false
                    }
                })
            }
        }
    }

    private fun onChangeQuery(query: String) {
        queryTextChangedJob?.cancel()
        queryTextChangedJob = lifecycleScope.launch(Dispatchers.Main) {
            delay(150)
            getFilterViewModel().updateFilter(query)
            getFilter().filter(getFilterViewModel().getFilterString())
        }
    }

    fun clearFilterClickListener() {
        getLayoutChipGroup().imgClear.setOnClickListener {
            getFilterViewModel().clearFilter()
            getFilter().filter(getFilterViewModel().getFilterString())
        }
    }

    fun observeFilterState() {

        launchAndRepeatWithLifecycle {
            getFilterViewModel().filterState.collect { filterState ->
                if (BuildConfig.FLAG_SEARCH_RECORDS) {
                    getSearchBar().searchRecords.apply {
                        if (query.toString() != filterState.search) {
                            setQuery(filterState.search, false)
                        }
                    }
                }
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
