package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentIndividualHealthRecordBinding
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordPlaceholderFragment
import ca.bc.gov.bchealth.ui.healthrecord.NavigationAction
import ca.bc.gov.bchealth.ui.healthrecord.filter.FilterUiState
import ca.bc.gov.bchealth.ui.healthrecord.filter.FilterViewModel
import ca.bc.gov.bchealth.ui.healthrecord.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.ui.healthrecord.protectiveword.HiddenMedicationRecordAdapter
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthState
import ca.bc.gov.bchealth.utils.hide
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class IndividualHealthRecordFragment : Fragment(R.layout.fragment_individual_health_record) {

    private val binding by viewBindings(FragmentIndividualHealthRecordBinding::bind)
    private val viewModel: IndividualHealthRecordViewModel by viewModels()
    private lateinit var hiddenMedicationRecordsAdapter: HiddenMedicationRecordAdapter
    private lateinit var hiddenHealthRecordAdapter: HiddenHealthRecordAdapter
    private lateinit var healthRecordsAdapter: HealthRecordsAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val filterSharedViewModel: FilterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(
                            HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
                            NavigationAction.ACTION_BACK
                        )
                    findNavController().popBackStack()
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        handleBcscAuthResponse()

        observeHealthRecords()

        observeHealthRecordsSyncCompletion()

        clearFilterClickListener()

        observeFilterState()

        observeNavigationFlow()
    }

    private fun setToolBar(bcscAuthenticatedPatientName: String) {
        val names = bcscAuthenticatedPatientName.split(" ")
        val firstName = if (names.isNotEmpty()) names.first() else ""
        binding.topAppBar1.apply {
            title = firstName
            if (willNotDraw()) {
                setWillNotDraw(false)
                inflateMenu(R.menu.menu_individual_health_record)
                setOnMenuItemClickListener { menu ->
                    when (menu.itemId) {
                        R.id.menu_filter -> {
                            findNavController().navigate(R.id.filterFragment)
                        }
                        R.id.menu_settings -> {
                            findNavController().navigate(R.id.profileFragment)
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
            }
        }
    }

    private fun updateTypeFilterSelection(filterUiState: FilterUiState) {
        resetFilters()
        filterUiState.timelineTypeFilter.forEach {
            when (it) {
                TimelineTypeFilter.MEDICATION.name -> {
                    binding.content.chipGroup.chipMedication.show()
                }
                TimelineTypeFilter.IMMUNIZATION.name -> {
                    binding.content.chipGroup.chipImmunizations.show()
                }
                TimelineTypeFilter.COVID_19_TEST.name -> {
                    binding.content.chipGroup.chipCovidTest.show()
                }
                TimelineTypeFilter.LAB_TEST.name -> {
                    binding.content.chipGroup.chipLabTest.show()
                }
            }
        }
    }

    private fun resetFilters() {
        binding.content.chipGroup.chipMedication.hide()
        binding.content.chipGroup.chipImmunizations.hide()
        binding.content.chipGroup.chipCovidTest.hide()
        binding.content.chipGroup.chipLabTest.hide()
    }

    private fun clearFilterClickListener() {
        binding.content.chipGroup.imgClear.setOnClickListener {
            filterSharedViewModel.updateFilter(listOf(TimelineTypeFilter.ALL.name), null, null)

            updateHiddenMedicationRecordsView(viewModel.uiState.value)

            healthRecordsAdapter.filter.filter(getFilterString())
        }
    }

    private fun handleBcscAuthResponse() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BcscAuthState>(
            BcscAuthFragment.BCSC_AUTH_STATUS
        )?.observe(viewLifecycleOwner) {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<BcscAuthState>(
                BcscAuthFragment.BCSC_AUTH_STATUS
            )
            when (it) {
                BcscAuthState.SUCCESS,
                BcscAuthState.NO_ACTION -> {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
                        NavigationAction.ACTION_RE_CHECK
                    )
                    findNavController().popBackStack()
                }
                else -> {
                    // no implementation required
                }
            }
        }
    }

    private fun getHiddenRecordItem(): ArrayList<HiddenRecordItem> {
        return arrayListOf(HiddenRecordItem(0))
    }

    private fun observeHealthRecordsSyncCompletion() {
        val workRequest = WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME)
        if (!workRequest.hasObservers()) {
            workRequest.observe(viewLifecycleOwner) {
                if (it.firstOrNull()?.state == WorkInfo.State.SUCCEEDED) {
                    viewModel.getIndividualsHealthRecord()
                }

                if (it.firstOrNull()?.state == WorkInfo.State.RUNNING) {
                    binding.emptyView.tvNoRecord.text = getString(R.string.fetching_records)
                    binding.emptyView.tvClearFilterMsg.text = ""
                } else {
                    binding.emptyView.tvNoRecord.text = getString(R.string.no_records_found)
                    binding.emptyView.tvClearFilterMsg.text =
                        getString(R.string.clear_all_filters_and_start_over)
                }
            }
        }
    }

    private fun observeHealthRecords() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.isBcscAuthenticatedPatientAvailable != null &&
                        uiState.isBcscAuthenticatedPatientAvailable
                    ) {
                        updateUi(uiState)
                    }
                }
            }
        }
    }

    private fun updateUi(uiState: IndividualHealthRecordsUiState) {
        uiState.bcscAuthenticatedPatientDto?.let {
            setToolBar(it.fullName)
        }
        if (uiState.isBcscSessionActive != null && uiState.isBcscSessionActive) {
            concatAdapter = ConcatAdapter(
                hiddenMedicationRecordsAdapter,
                healthRecordsAdapter
            )
            binding.content.rvHealthRecords.adapter = concatAdapter
            displayBcscRecords(uiState)
        } else {
            //clear filter when session is expired
            filterSharedViewModel.updateFilter(listOf(TimelineTypeFilter.ALL.name), null, null)
            concatAdapter = ConcatAdapter(
                hiddenHealthRecordAdapter
            )
            binding.content.rvHealthRecords.adapter = concatAdapter
            hiddenHealthRecordAdapter.submitList(getHiddenRecordItem())
        }
    }

    private fun displayBcscRecords(uiState: IndividualHealthRecordsUiState) {
        if (filterSharedViewModel.filterState.value.timelineTypeFilter.contains(
                TimelineTypeFilter.ALL.name
            ) ||
            filterSharedViewModel.filterState.value.timelineTypeFilter.contains(
                    TimelineTypeFilter.MEDICATION.name
                )
        ) {
            updateHiddenMedicationRecordsView(uiState)
        } else {
            if (::hiddenMedicationRecordsAdapter.isInitialized) {
                hiddenMedicationRecordsAdapter.submitList(emptyList())
            }
        }
        if (::healthRecordsAdapter.isInitialized) {
            healthRecordsAdapter.setData(uiState.onHealthRecords)
        }
        healthRecordsAdapter.filter.filter(getFilterString())
    }

    private fun updateHiddenMedicationRecordsView(uiState: IndividualHealthRecordsUiState) {
        if (viewModel.isShowMedicationRecords()) {
            if (::hiddenMedicationRecordsAdapter.isInitialized) {
                hiddenMedicationRecordsAdapter.submitList(emptyList())
            }
        } else {
            hiddenMedicationRecordsAdapter.submitList(
                uiState.bcscAuthenticatedPatientDto?.id?.let {
                    listOf(
                        HiddenMedicationRecordItem(
                            uiState.bcscAuthenticatedPatientDto.id,
                            getString(R.string.hidden_medication_records),
                            getString(R.string.enter_protective_word_to_access_medication_records)
                        )
                    )
                }
            )
        }
    }

    private fun setUpRecyclerView() {
        healthRecordsAdapter = HealthRecordsAdapter {
            when (it.healthRecordType) {
                HealthRecordType.VACCINE_RECORD -> {
                    val action = IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToVaccineRecordDetailFragment(
                            it.patientId
                        )
                    findNavController().navigate(action)
                }
                HealthRecordType.COVID_TEST_RECORD -> {

                    val action = if (it.covidOrderId != null) {
                        IndividualHealthRecordFragmentDirections.actionIndividualHealthRecordFragmentToCovidTestResultDetailFragment(
                            it.covidOrderId
                        )
                    } else {
                        IndividualHealthRecordFragmentDirections
                            .actionIndividualHealthRecordFragmentToTestResultDetailFragment(
                                it.patientId,
                                it.testResultId
                            )
                    }
                    findNavController().navigate(action)
                }
                HealthRecordType.MEDICATION_RECORD -> {
                    val action = IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToMedicationDetailFragment(
                            it.medicationRecordId
                        )
                    findNavController().navigate(action)
                }
                HealthRecordType.LAB_TEST -> {
                    it.labOrderId.let { it1 ->
                        val action = IndividualHealthRecordFragmentDirections
                            .actionIndividualHealthRecordFragmentToLabTestDetailFragment(
                                it1
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }

        hiddenHealthRecordAdapter = HiddenHealthRecordAdapter { onBCSCLoginClick() }
        hiddenMedicationRecordsAdapter = HiddenMedicationRecordAdapter {
            onMedicationAccessClick(it)
        }

        concatAdapter = ConcatAdapter(
            hiddenHealthRecordAdapter,
            hiddenMedicationRecordsAdapter,
            healthRecordsAdapter
        )
        binding.content.rvHealthRecords.adapter = concatAdapter
        binding.content.rvHealthRecords.layoutManager = LinearLayoutManager(requireContext())
        binding.content.rvHealthRecords.emptyView = binding.emptyView.root
    }

    private fun onMedicationAccessClick(patientId: Long) {
        val action = IndividualHealthRecordFragmentDirections
            .actionIndividualHealthRecordFragmentToProtectiveWordFragment(
                patientId
            )
        findNavController().navigate(action)
    }

    private fun onBCSCLoginClick() {
        sharedViewModel.destinationId = 0
        findNavController().navigate(R.id.bcscAuthInfoFragment)
    }

    private fun observeFilterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                filterSharedViewModel.filterState.collect { filterState ->

                    // update filter date selection
                    if (isFilterDateSelected(filterState)) {
                        binding.content.chipGroup.chipDate.apply {
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
                        binding.content.chipGroup.chipDate.hide()
                    }

                    updateTypeFilterSelection(filterState)

                    updateClearButton(filterState)
                }
            }
        }
    }

    private fun isFilterDateSelected(filterState: FilterUiState): Boolean {
        if (filterState.filterFromDate.isNullOrBlank() && filterState.filterToDate.isNullOrBlank()) {
            return false
        }
        return true
    }

    private fun updateClearButton(filterState: FilterUiState) {
        if (!isFilterDateSelected(filterState) && filterState.timelineTypeFilter.contains(
                TimelineTypeFilter.ALL.name
            )
        ) {
            binding.content.chipGroup.imgClear.hide()
        } else {
            binding.content.chipGroup.imgClear.show()
        }
    }

    private fun observeNavigationFlow() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<NavigationAction>(
            HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION
        )?.observe(viewLifecycleOwner) {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<NavigationAction>(
                HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION
            )
            it?.let {
                when (it) {
                    NavigationAction.ACTION_BACK -> {
                        // no implementation required
                    }
                    NavigationAction.ACTION_RE_CHECK -> {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
                            NavigationAction.ACTION_RE_CHECK
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun getFilterString(): String {
        var filterString =
            filterSharedViewModel.filterState.value.timelineTypeFilter.joinToString(",")
        if (filterSharedViewModel.filterState.value.filterFromDate != null) {
            filterString = filterString.plus(",FROM:")
                .plus(filterSharedViewModel.filterState.value.filterFromDate)
        }
        if (filterSharedViewModel.filterState.value.filterToDate != null) {
            filterString =
                filterString.plus(",TO:").plus(filterSharedViewModel.filterState.value.filterToDate)
        }

        return filterString
    }
}
