package ca.bc.gov.bchealth.ui.filter

import android.os.Bundle
import android.view.View
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFilterBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.DatePickerHelper
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.utils.toDate
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

abstract class FilterFragment : BaseFragment(R.layout.fragment_filter) {

    private val binding by viewBindings(FragmentFilterBinding::bind)
    abstract val filterSharedViewModel: FilterViewModel

    abstract val availableFilters: List<Int>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cgFilterByType.forEach { chip ->
            chip.toggleVisibility(availableFilters.contains(chip.id))
        }

        applyClickListener()

        clearClickListener()

        observeFilterState()
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = getString(R.string.filter)
        }
    }

    private fun applyClickListener() {
        binding.btnApply.setOnClickListener {
            if (validateSelectedDate()) {
                val filterList = mutableListOf<String>()
                val checkedChipIds = binding.cgFilterByType.checkedChipIds

                TimelineTypeFilter.values().forEach {
                    if (checkedChipIds.contains(it.id)) {
                        filterList.add(it.name)
                    }
                }

                if (filterList.isEmpty()) {
                    filterList.add(TimelineTypeFilter.ALL.name)
                }

                filterSharedViewModel.updateFilter(
                    filterList,
                    binding.etFrom.text.toString(),
                    binding.etTo.text.toString()
                )

                findNavController().popBackStack()
            } else {
                DatePickerHelper().updateErrorMessage(
                    binding.tipFrom,
                    requireContext().getString(R.string.date_filter_validation_msg)
                )
            }
        }
    }

    private fun validateSelectedDate(): Boolean {
        if (DatePickerHelper().validateDatePickerData(
                textInputLayout = binding.tipFrom,
                isBlankAllowed = true
            ) && DatePickerHelper().validateDatePickerData(
                    textInputLayout = binding.tipTo,
                    isBlankAllowed = true
                )
        ) {
            if (!binding.etFrom.text.toString().isNullOrBlank() &&
                !binding.etTo.text.toString().isNullOrBlank() &&
                binding.etFrom.text.toString().toDate()
                    .isAfter(binding.etTo.text.toString().toDate())
            ) {
                return false
            }
            return true
        }
        return true
    }

    private fun clearClickListener() {
        binding.btnClear.setOnClickListener {
            filterSharedViewModel.clearFilter()
            findNavController().popBackStack()
        }
    }

    private fun observeFilterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                filterSharedViewModel.filterState.collect { filterState ->

                    binding.etFrom.setText(filterState.filterFromDate)
                    binding.etTo.setText(filterState.filterToDate)

                    DatePickerHelper().initFilterDatePicker(
                        binding.tipFrom,
                        getString(R.string.enter_from_date),
                        parentFragmentManager,
                        "FILTER_FROM_DATE",
                        filterState.filterFromDate
                    )

                    DatePickerHelper().initFilterDatePicker(
                        binding.tipTo,
                        getString(R.string.enter_to_date),
                        parentFragmentManager,
                        "FILTER_TO_DATE",
                        filterState.filterToDate
                    )

                    initTypeFilter(filterState)
                }
            }
        }
    }

    private fun initTypeFilter(uiState: FilterUiState) {
        uiState.timelineTypeFilter.forEach { filterName ->
            TimelineTypeFilter.findByName(filterName)?.let { typeFilter ->
                typeFilter.id?.let {
                    val chip = view?.findViewById<Chip>(it)
                    chip?.isChecked = true
                }
            }
        }
    }
}
