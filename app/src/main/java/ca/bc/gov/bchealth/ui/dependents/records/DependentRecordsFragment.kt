package ca.bc.gov.bchealth.ui.dependents.records

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentRecordsBinding
import ca.bc.gov.bchealth.ui.dependents.records.filter.DependentFilterViewModel
import ca.bc.gov.bchealth.ui.healthrecord.BaseRecordFilterFragment
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordsAdapter
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.BuildConfig.FLAG_MANUAL_REFRESH
import ca.bc.gov.common.BuildConfig.FLAG_SEARCH_RECORDS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentRecordsFragment : BaseRecordFilterFragment(R.layout.fragment_dependent_records) {
    private val binding by viewBindings(FragmentDependentRecordsBinding::bind)
    private val args: DependentRecordsFragmentArgs by navArgs()
    private lateinit var healthRecordsAdapter: HealthRecordsAdapter
    private val viewModel: DependentRecordsViewModel by viewModels()
    private val filterSharedViewModel: DependentFilterViewModel by activityViewModels()

    override fun getSearchBar() = binding.content.searchBar
    override fun getFilterViewModel() = filterSharedViewModel
    override fun getFilter() = healthRecordsAdapter.filter
    override fun getLayoutChipGroup() = binding.content.chipGroup
    override fun getFilterFragmentId() = R.id.dependentFilterFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwipeToRefresh()
        setUpRecyclerView()
        setupSearchView()

        launchOnStart { observeUiState() }
        viewModel.loadRecords(patientId = args.patientId, hdid = args.hdid)
        clearFilterClickListener()
        observeFilterState()

        binding.content.searchBar.layoutSearch.isVisible = FLAG_SEARCH_RECORDS
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            binding.apply {

                if (!uiState.isConnected) {
                    root.showNoInternetConnectionMessage(requireContext())
                    viewModel.onNetworkDialogDisplayed()
                    content.srHealthRecords.isRefreshing = false
                }
                if (uiState.isHgServicesUp == false) {
                    root.showServiceDownMessage(requireContext())
                    viewModel.resetUiState()
                    content.srHealthRecords.isRefreshing = false
                } else {
                    with(content.srHealthRecords) {
                        isEnabled = FLAG_MANUAL_REFRESH
                        progressBar.indicator.toggleVisibility(uiState.onLoading && isRefreshing.not())
                        if (isRefreshing && uiState.records.isNotEmpty()) {
                            isRefreshing = false
                        }
                    }
                    healthRecordsAdapter.setData(uiState.records)
                    healthRecordsAdapter.filter.filter(filterSharedViewModel.getFilterString())
                    binding.content.searchBar.layoutSearch.isVisible = uiState.records.isNotEmpty()

                    binding.emptyView.apply {
                        if (uiState.onLoading) {
                            tvNoRecord.text = getString(R.string.fetching_records)
                            tvClearFilterMsg.text = ""
                        } else {
                            tvNoRecord.text = getString(R.string.no_records_found)
                            tvClearFilterMsg.text =
                                getString(R.string.clear_all_filters_and_start_over)
                        }
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        healthRecordsAdapter = HealthRecordsAdapter {

            val navDirection = when (it.healthRecordType) {
                HealthRecordType.COVID_TEST_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToCovidTestResultDetailFragment(it.recordId)

                HealthRecordType.MEDICATION_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToMedicationDetailFragment(it.recordId)

                HealthRecordType.LAB_RESULT_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToLabTestDetailFragment(
                            it.recordId,
                            args.hdid
                        )

                HealthRecordType.IMMUNIZATION_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToImmunizationRecordDetailFragment(it.recordId)

                HealthRecordType.HEALTH_VISIT_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToHealthVisitDetailsFragment(it.recordId)

                HealthRecordType.SPECIAL_AUTHORITY_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToSpecialAuthorityDetailsFragment(it.recordId)

                HealthRecordType.HOSPITAL_VISITS_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToHospitalVisitDetailsFragment(it.recordId)

                HealthRecordType.CLINICAL_DOCUMENT_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToClinicalDocsDetailsFragment(
                            it.recordId,
                            args.hdid
                        )

                HealthRecordType.DIAGNOSTIC_IMAGING -> null
            }

            navDirection?.let { findNavController().navigate(navDirection) }
        }
        binding.content.rvHealthRecords.layoutManager = LinearLayoutManager(requireContext())
        binding.content.rvHealthRecords.adapter = healthRecordsAdapter
        binding.content.rvHealthRecords.emptyView = binding.emptyView.root
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            title = args.fullName
            isTitleCentered = false
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            inflateMenu(R.menu.menu_dependent_health_record)
            menu.findItem(R.id.menu_refresh).isVisible = FLAG_MANUAL_REFRESH
            menu.findItem(R.id.menu_filter).isVisible = FLAG_SEARCH_RECORDS.not()
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {

                    R.id.menu_refresh -> {
                        healthRecordsAdapter.setData(emptyList())
                        with(binding.content.srHealthRecords) {
                            if (isRefreshing.not()) {
                                isRefreshing = true
                                viewModel.refresh(args.patientId, args.hdid)
                            }
                        }
                    }
                    R.id.menu_profile -> {
                        findNavController().navigate(
                            R.id.dependentProfileFragment,
                            bundleOf("patient_id" to args.patientId)
                        )
                    }
                    R.id.menu_filter -> {
                        findNavController().navigate(R.id.dependentFilterFragment)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun setupSwipeToRefresh() {
        with(binding.content.srHealthRecords) {
            setOnRefreshListener {
                if (FLAG_MANUAL_REFRESH) {
                    viewModel.refresh(args.patientId, args.hdid)
                }
            }
        }
    }
}
