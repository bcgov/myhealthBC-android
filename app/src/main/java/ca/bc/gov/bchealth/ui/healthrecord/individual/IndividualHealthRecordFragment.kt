package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentIndividualHealthRecordBinding
import ca.bc.gov.bchealth.ui.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.ui.healthrecord.BaseRecordFilterFragment
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordPlaceholderFragment
import ca.bc.gov.bchealth.ui.healthrecord.NavigationAction
import ca.bc.gov.bchealth.ui.healthrecord.filter.PatientFilterViewModel
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.CLINICAL_DOCUMENT_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.COVID_TEST_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.HEALTH_VISIT_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.HOSPITAL_VISITS_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.IMMUNIZATION_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.LAB_RESULT_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.MEDICATION_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType.SPECIAL_AUTHORITY_RECORD
import ca.bc.gov.bchealth.ui.healthrecord.protectiveword.HiddenMedicationRecordAdapter
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthState
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.hide
import ca.bc.gov.bchealth.utils.hideKeyboard
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.observeWork
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showNoInternetConnectionMessage
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import ca.bc.gov.common.BuildConfig.FLAG_IMMZ_BANNER
import ca.bc.gov.common.BuildConfig.FLAG_MANUAL_REFRESH
import ca.bc.gov.common.BuildConfig.FLAG_SEARCH_RECORDS
import ca.bc.gov.repository.bcsc.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class IndividualHealthRecordFragment :
    BaseRecordFilterFragment(R.layout.fragment_individual_health_record) {

    private val binding by viewBindings(FragmentIndividualHealthRecordBinding::bind)
    private val viewModel: IndividualHealthRecordViewModel by viewModels()
    private lateinit var hiddenMedicationRecordsAdapter: HiddenMedicationRecordAdapter
    private lateinit var hiddenHealthRecordAdapter: HiddenHealthRecordAdapter
    private lateinit var healthRecordsAdapter: HealthRecordsAdapter
    private lateinit var immunizationBannerAdapter: ImmunizationBannerAdapter
    private lateinit var concatAdapter: ConcatAdapter

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val filterSharedViewModel: PatientFilterViewModel by activityViewModels()

    override fun getSearchBar() = binding.content.searchBar
    override fun getFilterViewModel() = filterSharedViewModel
    override fun getFilterFragmentId() = R.id.filterFragment

    override fun getFilter() = healthRecordsAdapter.filter
    override fun getLayoutChipGroup() = binding.content.chipGroup

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

        setupSearchView()
        setupSwipeToRefresh()
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
                        R.id.menu_refresh -> {
                            with(binding.content.srHealthRecords) {
                                if (isRefreshing.not()) {
                                    isRefreshing = true
                                    viewModel.executeOneTimeDataFetch()
                                }
                            }
                        }
                        R.id.menu_settings -> {
                            findNavController().navigate(R.id.settingsFragment)
                        }
                        R.id.menu_filter -> {
                            findNavController().navigate(R.id.filterFragment)
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
            }
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
        observeWork(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME) { state ->
            if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED) {
                hiddenHealthRecordAdapter.submitList(emptyList())
                healthRecordsAdapter.submitList(emptyList())
                hiddenMedicationRecordsAdapter.submitList(emptyList())
                binding.emptyView.tvNoRecord.text = getString(R.string.fetching_records)
                binding.emptyView.tvClearFilterMsg.text = ""

                binding.content.searchBar.layoutSearch.hide()
            } else {
                binding.content.srHealthRecords.isRefreshing = false
                binding.emptyView.tvNoRecord.text = getString(R.string.no_records_found)
                binding.emptyView.tvClearFilterMsg.text =
                    getString(R.string.clear_all_filters_and_start_over)
                viewModel.getIndividualsHealthRecord()
            }
        }
    }

    private fun observeHealthRecords() {
        launchOnStart {
            viewModel.uiState.collect { uiState ->
                if (uiState.isBcscAuthenticatedPatientAvailable == true) {
                    updateUi(uiState)
                }
            }
        }
    }

    private fun updateUi(uiState: IndividualHealthRecordsUiState) {
        if (!uiState.isHgServicesUp) {
            binding.root.showServiceDownMessage(requireContext())
            binding.content.srHealthRecords.isRefreshing = false
            viewModel.resetErrorState()
        }

        if (!uiState.isConnected) {
            binding.content.srHealthRecords.isRefreshing = false
            binding.root.showNoInternetConnectionMessage(requireContext())
            viewModel.resetErrorState()
        }

        uiState.bcscAuthenticatedPatientDto?.let {
            setToolBar(it.fullName)
        }

        binding.content.searchBar.layoutSearch.isVisible =
            uiState.isBcscSessionActive == true && FLAG_SEARCH_RECORDS && uiState.onHealthRecords.isNotEmpty()

        with(binding.topAppBar1.menu) {
            findItem(R.id.menu_refresh).isVisible =
                uiState.isBcscSessionActive == true && FLAG_MANUAL_REFRESH

            findItem(R.id.menu_filter).isVisible =
                uiState.isBcscSessionActive == true && FLAG_SEARCH_RECORDS.not()
        }

        binding.content.srHealthRecords.isEnabled =
            uiState.isBcscSessionActive == true && FLAG_MANUAL_REFRESH

        if (uiState.isBcscSessionActive == true) {
            val adapters = arrayListOf<RecyclerView.Adapter<out RecyclerView.ViewHolder?>>()

            if (FLAG_IMMZ_BANNER && sharedViewModel.displayImmunizationBanner) {
                adapters.add(immunizationBannerAdapter)
            }
            adapters.add(hiddenMedicationRecordsAdapter)
            adapters.add(healthRecordsAdapter)

            concatAdapter = ConcatAdapter(adapters)
            binding.content.rvHealthRecords.adapter = concatAdapter
            displayBcscRecords(uiState)
        } else {
            // clear filter when session is expired
            filterSharedViewModel.clearFilter()
            concatAdapter = ConcatAdapter(
                hiddenHealthRecordAdapter
            )
            binding.content.rvHealthRecords.adapter = concatAdapter
            hiddenHealthRecordAdapter.submitList(getHiddenRecordItem())
        }
    }

    private fun displayBcscRecords(uiState: IndividualHealthRecordsUiState) {
        val timelineTypeFilter = filterSharedViewModel.filterState.value.timelineTypeFilter
        if (timelineTypeFilter.contains(TimelineTypeFilter.ALL.name) ||
            timelineTypeFilter.contains(TimelineTypeFilter.MEDICATION.name)
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
        healthRecordsAdapter.filter.filter(filterSharedViewModel.getFilterString())
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpRecyclerView() {
        healthRecordsAdapter = HealthRecordsAdapter {
            val navDirection = when (it.healthRecordType) {
                COVID_TEST_RECORD ->
                    IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToCovidTestResultDetailFragment(it.recordId)

                MEDICATION_RECORD ->
                    IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToMedicationDetailFragment(it.recordId)

                LAB_RESULT_RECORD ->
                    IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToLabTestDetailFragment(it.recordId)

                IMMUNIZATION_RECORD ->
                    IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToImmunizationRecordDetailFragment(
                            it.recordId
                        )

                HEALTH_VISIT_RECORD ->
                    IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToHealthVisitDetailsFragment(it.recordId)

                SPECIAL_AUTHORITY_RECORD ->
                    IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToSpecialAuthorityDetailsFragment(
                            it.recordId
                        )

                HOSPITAL_VISITS_RECORD ->
                    IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordsFragmentToHospitalVisitDetailsFragment(it.recordId)

                CLINICAL_DOCUMENT_RECORD ->
                    IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordsFragmentToClinicalDocumentDetailsFragment(
                            it.recordId
                        )
            }

            findNavController().navigate(navDirection)
        }

        hiddenHealthRecordAdapter = HiddenHealthRecordAdapter { onBCSCLoginClick() }
        hiddenMedicationRecordsAdapter = HiddenMedicationRecordAdapter {
            onMedicationAccessClick(it)
        }

        immunizationBannerAdapter = ImmunizationBannerAdapter(
            onClickLink = ::openImmunizationPage,
            onClickClose = ::showResourcesDialog
        )

        concatAdapter = ConcatAdapter(
            hiddenHealthRecordAdapter,
            hiddenMedicationRecordsAdapter,
            healthRecordsAdapter
        )
        binding.content.rvHealthRecords.apply {
            adapter = concatAdapter
            layoutManager = LinearLayoutManager(requireContext())
            emptyView = binding.emptyView.root
            excludeAdapterFromEmptyCount(immunizationBannerAdapter)
            setOnTouchListener { v, _ ->
                requireActivity().hideKeyboard(v)
                false
            }
        }
    }

    private fun openImmunizationPage() {
        requireActivity().redirect(getString(R.string.url_update_your_immnz))
    }

    private fun showResourcesDialog() {
        AlertDialogHelper.showAlertDialog(
            context = requireContext(),
            title = "",
            msg = getString(R.string.records_dialog_resources_content),
            positiveBtnMsg = getString(R.string.records_dialog_resources_button),
            positiveBtnCallback = ::closeBanner,
            cancelable = true
        )
    }

    private fun closeBanner() {
        concatAdapter.removeAdapter(immunizationBannerAdapter)
        sharedViewModel.displayImmunizationBanner = false
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

    private fun setupSwipeToRefresh() {
        with(binding.content.srHealthRecords) {
            setOnRefreshListener {
                if (FLAG_MANUAL_REFRESH) {
                    viewModel.executeOneTimeDataFetch()
                }
            }
        }
    }
}
