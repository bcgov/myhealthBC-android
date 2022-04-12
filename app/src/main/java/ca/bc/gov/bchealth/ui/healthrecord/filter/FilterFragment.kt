package ca.bc.gov.bchealth.ui.healthrecord.filter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFilterBinding
import ca.bc.gov.bchealth.utils.DatePickerHelper
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.utils.toDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterFragment : Fragment(R.layout.fragment_filter) {

    private val binding by viewBindings(FragmentFilterBinding::bind)
    private val filterSharedViewModel: FilterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar()

        applyClickListener()

        clearClickListener()

        observeFilterState()
    }

    private fun setUpToolbar() {
        binding.toolbar.apply {
            tvTitle.show()
            tvTitle.text = getString(R.string.filter)
            line1.visibility = View.VISIBLE
            ivLeftOption.apply {
                this.show()
                setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun initTypeFilter(filterState: FilterUiState) {
        filterState.timelineTypeFilter.forEach {
            when (it) {
                TimelineTypeFilter.MEDICATION -> {
                    binding.chipMedication.isChecked = true
                }
                TimelineTypeFilter.IMMUNIZATION -> {
                    binding.chipImmunizations.isChecked = true
                }
                TimelineTypeFilter.COVID_19_TEST -> {
                    binding.chipCovidTest.isChecked = true
                }
                TimelineTypeFilter.LAB_TEST -> {
                    binding.chipLabTest.isChecked = true
                }
            }
        }
    }

    private fun applyClickListener() {
        binding.btnApply.setOnClickListener {
            if (validateSelectedDate()) {
                val filterList = mutableListOf<TimelineTypeFilter>()
                val checkedChipIds = binding.cgFilterByType.checkedChipIds
                checkedChipIds.forEach {
                    when (it) {
                        R.id.chip_medication -> {
                            filterList.add(TimelineTypeFilter.MEDICATION)
                        }
                        R.id.chip_lab_test -> {
                            filterList.add(TimelineTypeFilter.LAB_TEST)
                        }
                        R.id.chip_covid_test -> {
                            filterList.add(TimelineTypeFilter.COVID_19_TEST)
                        }
                        R.id.chip_immunizations -> {
                            filterList.add(TimelineTypeFilter.IMMUNIZATION)
                        }
                    }
                }
                if (filterList.isNullOrEmpty()) {
                    filterList.add(TimelineTypeFilter.ALL)
                }

                filterSharedViewModel.updateFilter(filterList, binding.etFrom.text.toString(), binding.etTo.text.toString())

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
                context = requireContext(),
                errorMessage = "",
                isBlankAllowed = true
            ) && DatePickerHelper().validateDatePickerData(
                    textInputLayout = binding.tipTo,
                    context = requireContext(),
                    errorMessage = "",
                    isBlankAllowed = true
                )
        ) {
            if (!binding.etFrom.text.toString().isNullOrBlank() &&
                !binding.etTo.text.toString().isNullOrBlank() &&
                binding.etFrom.text.toString().toDate().isAfter(binding.etTo.text.toString().toDate())
            ) {
                return false
            }
            return true
        }
        return true
    }

    private fun clearClickListener() {
        binding.btnClear.setOnClickListener {
            filterSharedViewModel.updateFilter(mutableListOf(TimelineTypeFilter.ALL), null, null)

            findNavController().popBackStack()
        }
    }

    private fun observeFilterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                filterSharedViewModel.filterState.collect { filterState ->

                    binding.etFrom.setText(filterState.filterFromDate)
                    binding.etTo.setText(filterState.filterToDate)

                    DatePickerHelper().initFilterDatePicker(
                        binding.tipFrom,
                        getString(R.string.select_date),
                        parentFragmentManager,
                        "FILTER_FROM_DATE",
                        filterState.filterFromDate
                    )

                    DatePickerHelper().initFilterDatePicker(
                        binding.tipTo,
                        getString(R.string.select_date),
                        parentFragmentManager,
                        "FILTER_TO_DATE",
                        filterState.filterToDate
                    )

                    initTypeFilter(filterState)
                }
            }
        }
    }
}
