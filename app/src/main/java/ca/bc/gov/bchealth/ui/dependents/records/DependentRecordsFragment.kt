package ca.bc.gov.bchealth.ui.dependents.records

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentRecordsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.dependents.records.filter.DependentFilterViewModel
import ca.bc.gov.bchealth.ui.filter.FilterUiState
import ca.bc.gov.bchealth.ui.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordsAdapter
import ca.bc.gov.bchealth.utils.hide
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentRecordsFragment : BaseFragment(R.layout.fragment_dependent_records) {
    private val binding by viewBindings(FragmentDependentRecordsBinding::bind)
    private val args: DependentRecordsFragmentArgs by navArgs()
    private lateinit var healthRecordsAdapter: HealthRecordsAdapter
    private val viewModel: DependentRecordsViewModel by viewModels()
    private val filterSharedViewModel: DependentFilterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        launchOnStart { observeUiState() }
        viewModel.loadRecords(patientId = args.patientId, hdid = args.hdid)

        clearFilterClickListener()
        launchOnStart { observeFilterState() }
    }

    private fun clearFilterClickListener() {
        binding.chipGroup.imgClear.setOnClickListener {
            filterSharedViewModel.clearFilter()
            healthRecordsAdapter.filter.filter(filterSharedViewModel.getFilterString())
        }
    }

    private suspend fun observeFilterState() {
        filterSharedViewModel.filterState.collect { filterState ->

            // update filter date selection
            if (isFilterDateSelected(filterState)) {
                binding.chipGroup.chipDate.apply {
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
                binding.chipGroup.chipDate.hide()
            }

            updateTypeFilterSelection(filterState)

            updateClearButton(filterState)
        }
    }

    private fun updateClearButton(filterState: FilterUiState) {
        if (!isFilterDateSelected(filterState) && filterState.timelineTypeFilter.contains(
                TimelineTypeFilter.ALL.name
            )
        ) {
            binding.chipGroup.imgClear.hide()
        } else {
            binding.chipGroup.imgClear.show()
        }
    }

    private fun updateTypeFilterSelection(filterUiState: FilterUiState) {
        resetFilters()
        filterUiState.timelineTypeFilter.forEach {
            when (it) {
                TimelineTypeFilter.MEDICATION.name -> {
                    binding.chipGroup.chipMedication.show()
                }
                TimelineTypeFilter.IMMUNIZATION.name -> {
                    binding.chipGroup.chipImmunizations.show()
                }
                TimelineTypeFilter.COVID_19_TEST.name -> {
                    binding.chipGroup.chipCovidTest.show()
                }
                TimelineTypeFilter.LAB_TEST.name -> {
                    binding.chipGroup.chipLabTest.show()
                }
                TimelineTypeFilter.HEALTH_VISIT.name -> {
                    binding.chipGroup.chipHealthVisits.show()
                }
                TimelineTypeFilter.SPECIAL_AUTHORITY.name -> {
                    binding.chipGroup.chipSpecialAuthority.show()
                }
            }
        }
    }

    private fun resetFilters() {
        binding.chipGroup.chipMedication.hide()
        binding.chipGroup.chipImmunizations.hide()
        binding.chipGroup.chipCovidTest.hide()
        binding.chipGroup.chipLabTest.hide()
        binding.chipGroup.chipHealthVisits.hide()
        binding.chipGroup.chipSpecialAuthority.hide()
    }

    private fun isFilterDateSelected(filterState: FilterUiState): Boolean {
        if (filterState.filterFromDate.isNullOrBlank() && filterState.filterToDate.isNullOrBlank()) {
            return false
        }
        return true
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            binding.apply {
                if (uiState.isHgServicesUp == false) {
                    root.showServiceDownMessage(requireContext())
                    viewModel.resetUiState()
                } else {
                    progressBar.indicator.toggleVisibility(uiState.onLoading)
                    healthRecordsAdapter.setData(uiState.records)
                    healthRecordsAdapter.filter.filter(filterSharedViewModel.getFilterString())
                    rvHealthRecords.setLoading(uiState.onLoading)
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        healthRecordsAdapter = HealthRecordsAdapter {
            when (it.healthRecordType) {
                HealthRecordType.VACCINE_RECORD -> {
                    val action = DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToVaccineRecordDetailFragment(
                            it.patientId
                        )
                    findNavController().navigate(action)
                }
                HealthRecordType.COVID_TEST_RECORD -> {

                    val action = if (it.covidOrderId != null) {
                        DependentRecordsFragmentDirections.actionDependentRecordsFragmentToCovidTestResultDetailFragment(
                            it.covidOrderId
                        )
                    } else {
                        DependentRecordsFragmentDirections
                            .actionDependentRecordsFragmentToTestResultDetailFragment(
                                it.patientId,
                                it.testResultId
                            )
                    }
                    findNavController().navigate(action)
                }
                HealthRecordType.MEDICATION_RECORD -> {
                    val action = DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToMedicationDetailFragment(
                            it.medicationRecordId
                        )
                    findNavController().navigate(action)
                }
                HealthRecordType.LAB_TEST -> {
                    it.labOrderId.let { it1 ->
                        val action = DependentRecordsFragmentDirections
                            .actionDependentRecordsFragmentToLabTestDetailFragment(
                                it1
                            )
                        findNavController().navigate(action)
                    }
                }
                HealthRecordType.IMMUNIZATION_RECORD -> {
                    val action = DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToImmunizationRecordDetailFragment(
                            it.immunizationRecordId
                        )
                    findNavController().navigate(action)
                }
                HealthRecordType.HEALTH_VISIT_RECORD -> {
                    val action = DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToHealthVisitDetailsFragment(
                            it.healthVisitId
                        )
                    findNavController().navigate(action)
                }
                HealthRecordType.SPECIAL_AUTHORITY_RECORD -> {
                    val action = DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToSpecialAuthorityDetailsFragment(
                            it.specialAuthorityId
                        )
                    findNavController().navigate(action)
                }
            }
        }
        binding.rvHealthRecords.adapter = healthRecordsAdapter
        binding.rvHealthRecords.emptyView = binding.viewEmptyScreen
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        binding.layoutToolbar.apply {
            toolbar.stateListAnimator = null
            toolbar.elevation = 0f

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnFilter.setOnClickListener {
                findNavController().navigate(R.id.dependentFilterFragment)
            }

            tvTitle.text = args.fullName
        }
    }
}
