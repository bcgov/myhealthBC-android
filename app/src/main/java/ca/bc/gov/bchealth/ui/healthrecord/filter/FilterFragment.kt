package ca.bc.gov.bchealth.ui.healthrecord.filter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFilterBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.DatePickerHelper
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.utils.toDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterFragment : BaseFragment(R.layout.fragment_filter) {

    private val binding by viewBindings(FragmentFilterBinding::bind)
    private val filterSharedViewModel: FilterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    private fun initTypeFilter(filterState: FilterUiState) {
        filterState.timelineTypeFilter.forEach {
            when (it) {
                TimelineTypeFilter.MEDICATION.name -> {
                    binding.chipMedication.isChecked = true
                }
                TimelineTypeFilter.IMMUNIZATION.name -> {
                    binding.chipImmunizations.isChecked = true
                }
                TimelineTypeFilter.COVID_19_TEST.name -> {
                    binding.chipCovidTest.isChecked = true
                }
                TimelineTypeFilter.LAB_TEST.name -> {
                    binding.chipLabTest.isChecked = true
                }
                TimelineTypeFilter.HEALTH_VISIT.name -> {
                    binding.chipHealthVisit.isChecked = true
                }
                TimelineTypeFilter.SPECIAL_AUTHORITY.name -> {
                    binding.chipSpecialAuthority.isChecked = true
                }
            }
        }
    }

    private fun applyClickListener() {
        binding.btnApply.setOnClickListener {
            if (validateSelectedDate()) {
                val filterList = mutableListOf<String>()
                val checkedChipIds = binding.cgFilterByType.checkedChipIds
                checkedChipIds.forEach {
                    when (it) {
                        R.id.chip_medication -> {
                            filterList.add(TimelineTypeFilter.MEDICATION.name)
                        }
                        R.id.chip_lab_test -> {
                            filterList.add(TimelineTypeFilter.LAB_TEST.name)
                        }
                        R.id.chip_covid_test -> {
                            filterList.add(TimelineTypeFilter.COVID_19_TEST.name)
                        }
                        R.id.chip_immunizations -> {
                            filterList.add(TimelineTypeFilter.IMMUNIZATION.name)
                        }
                        R.id.chip_health_visit -> {
                            filterList.add(TimelineTypeFilter.HEALTH_VISIT.name)
                        }
                        R.id.chip_special_authority -> {
                            filterList.add(TimelineTypeFilter.SPECIAL_AUTHORITY.name)
                        }
                    }
                }
                if (filterList.isNullOrEmpty()) {
                    filterList.add(TimelineTypeFilter.ALL.name)
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
            filterSharedViewModel.updateFilter(mutableListOf(TimelineTypeFilter.ALL.name), null, null)

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
}
