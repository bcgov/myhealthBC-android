package ca.bc.gov.bchealth.ui.healthrecord.filter

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFilterBinding
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

const val KEY_FILTER_REQUEST = "KEY_FILTER_REQUEST"
const val KEY_FILTER_UPDATED = "KEY_FILTER_UPDATED"

@AndroidEntryPoint
class FilterFragment : Fragment(R.layout.fragment_filter) {

    private val binding by viewBindings(FragmentFilterBinding::bind)
    private val filterSharedViewModel: FilterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar()

        initTypeFilter()

        applyClickListener()

        clearClickListener()
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

    private fun initTypeFilter() {
        filterSharedViewModel.timelineTypeFilter.forEach {
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
                TimelineTypeFilter.ALL -> {
                    binding.chipMedication.isChecked = true
                    binding.chipImmunizations.isChecked = true
                    binding.chipCovidTest.isChecked = true
                    binding.chipLabTest.isChecked = true
                }
            }
        }
    }

    private fun applyClickListener() {
        binding.btnApply.setOnClickListener {
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
                filterList.add(TimelineTypeFilter.NONE)
            }
            filterSharedViewModel.timelineTypeFilter = filterList
            parentFragmentManager.setFragmentResult(
                KEY_FILTER_REQUEST,
                bundleOf(
                    KEY_FILTER_UPDATED to true,
                )
            )
            findNavController().popBackStack()
        }
    }

    private fun clearClickListener() {
        binding.btnClear.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
