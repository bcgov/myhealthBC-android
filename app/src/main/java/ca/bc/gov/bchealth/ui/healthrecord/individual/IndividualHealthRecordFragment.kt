package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import androidx.work.WorkManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentIndividualHealthRecordBinding
import ca.bc.gov.bchealth.ui.healthpass.add.FetchVaccineRecordFragment
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordPlaceholderFragment
import ca.bc.gov.bchealth.ui.healthrecord.NavigationAction
import ca.bc.gov.bchealth.ui.healthrecord.add.FetchTestRecordFragment
import ca.bc.gov.bchealth.ui.healthrecord.filter.FilterUiState
import ca.bc.gov.bchealth.ui.healthrecord.filter.FilterViewModel
import ca.bc.gov.bchealth.ui.healthrecord.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.ui.healthrecord.protectiveword.HiddenMedicationRecordAdapter
import ca.bc.gov.bchealth.ui.healthrecord.protectiveword.KEY_MEDICATION_RECORD_REQUEST
import ca.bc.gov.bchealth.ui.healthrecord.protectiveword.KEY_MEDICATION_RECORD_UPDATED
import ca.bc.gov.bchealth.ui.login.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthState
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginStatus
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.hide
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.SharedViewModel
import ca.bc.gov.common.model.AuthenticationStatus
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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
    private val args: IndividualHealthRecordFragmentArgs by navArgs()
    private var testResultId: Long = -1L
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var loginStatus: LoginStatus? = null
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

        handleBcscAuthResponse()

        setupToolbar()

        setUpRecyclerView()

        protectiveWordFragmentResultListener()

        observeBcscLogin()

        bcscAuthViewModel.checkLogin()

        observeHealthRecords()

        observeHealthRecordsSyncCompletion()

        observeVaccineRecordAddition()

        observeCovidTestRecordAddition()

        clearFilterClickListener()

        observeFilterState()
    }

    private fun updateTypeFilterSelection(filterUiState: FilterUiState) {
        resetFilters()
        filterUiState.timelineTypeFilter.forEach {
            when (it) {
                TimelineTypeFilter.MEDICATION -> {
                    binding.chipMedication.show()
                }
                TimelineTypeFilter.IMMUNIZATION -> {
                    binding.chipImmunizations.show()
                }
                TimelineTypeFilter.COVID_19_TEST -> {
                    binding.chipCovidTest.show()
                }
                TimelineTypeFilter.LAB_TEST -> {
                    binding.chipLabTest.show()
                }
            }
        }
    }

    private fun resetFilters() {
        binding.chipMedication.hide()
        binding.chipImmunizations.hide()
        binding.chipCovidTest.hide()
        binding.chipLabTest.hide()
    }

    private fun clearFilterClickListener() {
        binding.imgClear.setOnClickListener {
            filterSharedViewModel.updateFilter(listOf(TimelineTypeFilter.ALL), null, null)

            viewModel.getIndividualsHealthRecord(
                args.patientId,
                filterSharedViewModel.filterState.value.timelineTypeFilter,
                filterSharedViewModel.filterState.value.filterFromDate,
                filterSharedViewModel.filterState.value.filterToDate
            )
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
                BcscAuthState.SUCCESS -> {
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(FetchTestRecordFragment.TEST_RECORD_ADDED_SUCCESS, 1L)
                    findNavController().popBackStack()
                }
                else -> {
                    // no implementation required
                }
            }
        }
    }

    private fun getHiddenRecordItem(authenticatedRecordsCount: Int): ArrayList<HiddenRecordItem> {
        return arrayListOf(HiddenRecordItem(authenticatedRecordsCount))
    }

    private fun observeHealthRecordsSyncCompletion() {
        val workRequest = WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME)
        if (!workRequest.hasObservers()) {
            workRequest.observe(viewLifecycleOwner) {
                if (it.firstOrNull()?.state == WorkInfo.State.ENQUEUED) {
                    viewModel.getIndividualsHealthRecord(
                        args.patientId,
                        filterSharedViewModel.filterState.value.timelineTypeFilter,
                        filterSharedViewModel.filterState.value.filterFromDate,
                        filterSharedViewModel.filterState.value.filterToDate
                    )
                }
            }
        }
    }

    private fun observeBcscLogin() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bcscAuthViewModel.authStatus.collect { authStatus ->
                    if (authStatus.showLoading) {
                        return@collect
                    } else {
                        authStatus.loginStatus?.let {
                            loginStatus = it
                            viewModel.getIndividualsHealthRecord(
                                args.patientId,
                                filterSharedViewModel.filterState.value.timelineTypeFilter,
                                filterSharedViewModel.filterState.value.filterFromDate,
                                filterSharedViewModel.filterState.value.filterToDate
                            )
                        }
                    }
                    if (loginStatus == LoginStatus.EXPIRED) {
                        // clear timeline filter
                        filterSharedViewModel.updateFilter(listOf(TimelineTypeFilter.ALL), null, null)
                    }
                }
            }
        }
    }

    private fun observeHealthRecords() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    if (loginStatus != null) {
                        updateUi(uiState)
                    }

                    if (uiState.updatedTestResultId > 0) {
                        viewModel.getIndividualsHealthRecord(
                            args.patientId,
                            filterSharedViewModel.filterState.value.timelineTypeFilter,
                            filterSharedViewModel.filterState.value.filterFromDate,
                            filterSharedViewModel.filterState.value.filterToDate
                        )
                        return@collect
                    }

                    respondToQueueIt(uiState)
                }
            }
        }
    }

    private fun respondToQueueIt(uiState: IndividualHealthRecordsUiState) {
        if (uiState.queItTokenUpdated) {
            requestUpdate(testResultId)
        }

        if (uiState.onMustBeQueued && uiState.queItUrl != null) {
            queUser(uiState.queItUrl)
        }
    }

    private fun updateUi(uiState: IndividualHealthRecordsUiState) {
        binding.progressBar.isVisible = false

        updateHealthRecordsList(uiState)

        if (uiState.patientAuthStatus == AuthenticationStatus.AUTHENTICATED) {
            binding.ivEdit.visibility = View.GONE
            binding.ivFilter.visibility = View.VISIBLE
            healthRecordsAdapter.isUpdateRequested = false
        } else {
            binding.ivEdit.visibility = View.VISIBLE
            binding.ivFilter.visibility = View.GONE
            healthRecordsAdapter.isUpdateRequested = true
        }
    }

    private fun updateHealthRecordsList(uiState: IndividualHealthRecordsUiState) {
        if (loginStatus == LoginStatus.ACTIVE) {
            displayBcscRecords(uiState)
        }

        if (loginStatus == LoginStatus.EXPIRED) {
            displayNonBcscRecords(uiState)
        }
    }

    private fun displayNonBcscRecords(uiState: IndividualHealthRecordsUiState) {
        if (::healthRecordsAdapter.isInitialized) {
            healthRecordsAdapter.submitList(uiState.onNonBcscHealthRecords)
        }

        if (uiState.authenticatedRecordsCount != null &&
            uiState.patientAuthStatus == AuthenticationStatus.AUTHENTICATED &&
            ::hiddenHealthRecordAdapter.isInitialized
        ) {
            hiddenHealthRecordAdapter.submitList(
                getHiddenRecordItem(uiState.authenticatedRecordsCount)
            )
        }
    }

    private fun displayBcscRecords(uiState: IndividualHealthRecordsUiState) {
        if (filterSharedViewModel.filterState.value.timelineTypeFilter.contains(TimelineTypeFilter.ALL) || filterSharedViewModel.filterState.value.timelineTypeFilter.contains(
                TimelineTypeFilter.MEDICATION
            )
        ) {
            displayBCSCRecordsWithMedicationFilter(uiState)
        } else {
            displayBCSCRecordsExceptMedicationFilter(uiState)
        }
    }

    private fun displayBCSCRecordsWithMedicationFilter(uiState: IndividualHealthRecordsUiState) {
        if (uiState.medicationRecordsUpdated || !viewModel.isProtectiveWordRequired() || sharedViewModel.isProtectiveWordAdded) {
            if (::healthRecordsAdapter.isInitialized) {
                healthRecordsAdapter.submitList(uiState.onHealthRecords)
            }
            if (::hiddenMedicationRecordsAdapter.isInitialized) {
                hiddenMedicationRecordsAdapter.submitList(emptyList())
            }
        } else {
            if (::healthRecordsAdapter.isInitialized) {
                healthRecordsAdapter.submitList(uiState.healthRecordsExceptMedication)
            }
            if (::hiddenMedicationRecordsAdapter.isInitialized &&
                uiState.patientAuthStatus == AuthenticationStatus.AUTHENTICATED
            ) {
                hiddenMedicationRecordsAdapter.submitList(
                    listOf(
                        HiddenMedicationRecordItem(
                            getString(R.string.hidden_medication_records),
                            getString(R.string.enter_protective_word_to_access_medication_records)
                        )
                    )
                )
            }
        }
    }

    private fun displayBCSCRecordsExceptMedicationFilter(uiState: IndividualHealthRecordsUiState) {
        if (::healthRecordsAdapter.isInitialized) {
            healthRecordsAdapter.submitList(uiState.healthRecordsExceptMedication)
        }
        if (::hiddenMedicationRecordsAdapter.isInitialized) {
            hiddenMedicationRecordsAdapter.submitList(emptyList())
        }
    }

    private fun setUpRecyclerView() {
        healthRecordsAdapter = HealthRecordsAdapter(
            {
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
                        it.labOrderId?.let { it1 ->
                            val action = IndividualHealthRecordFragmentDirections
                                .actionIndividualHealthRecordFragmentToLabTestDetailFragment(
                                    it1
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            },
            {
                when (it.healthRecordType) {
                    HealthRecordType.VACCINE_RECORD,
                    HealthRecordType.COVID_TEST_RECORD -> {
                        showHealthRecordDeleteDialog(it)
                    }
                    HealthRecordType.MEDICATION_RECORD,
                    HealthRecordType.LAB_TEST -> {
                        // No implementation required
                    }
                }
            },
            {
                requestUpdate(it.testResultId)
            },
            isUpdateRequested = true,
            canDeleteRecord = false
        )

        hiddenHealthRecordAdapter = HiddenHealthRecordAdapter { onBCSCLoginClick() }
        hiddenMedicationRecordsAdapter = HiddenMedicationRecordAdapter { onMedicationAccessClick() }

        concatAdapter = ConcatAdapter(
            hiddenHealthRecordAdapter,
            hiddenMedicationRecordsAdapter,
            healthRecordsAdapter
        )
        binding.rvHealthRecords.adapter = concatAdapter
        binding.rvHealthRecords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHealthRecords.emptyView = binding.emptyView
    }

    private fun onMedicationAccessClick() {
        val action = IndividualHealthRecordFragmentDirections
            .actionIndividualHealthRecordFragmentToProtectiveWordFragment(
                args.patientId
            )
        findNavController().navigate(action)
    }

    private fun requestUpdate(testResultId: Long) {
        this.testResultId = testResultId
        viewModel.requestUpdate(testResultId)
    }

    private fun setupToolbar() {

        binding.apply {
            tvName.text = args.patientName
            if (findNavController().previousBackStackEntry?.destination?.id ==
                R.id.healthRecordsFragment
            ) {
                ivBack.visibility = View.VISIBLE
                ivBack.setOnClickListener {
                    findNavController().popBackStack()
                }
            } else {
                ivBack.visibility = View.GONE
            }
            ivSetting.setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
            ivAdd.setOnClickListener {
                val action = IndividualHealthRecordFragmentDirections
                    .actionIndividualHealthRecordFragmentToAddHealthRecordFragment(true)
                findNavController().navigate(action)
            }
            ivFilter.setOnClickListener {
                findNavController().navigate(R.id.filterFragment)
            }
            ivEdit.setOnClickListener {
                healthRecordsAdapter.canDeleteRecord = !healthRecordsAdapter.canDeleteRecord
                if (healthRecordsAdapter.canDeleteRecord) {
                    ivEdit.setImageResource(R.drawable.ic_done)
                } else {
                    ivEdit.setImageResource(R.drawable.ic_edit)
                }
                concatAdapter.notifyItemRangeChanged(
                    0,
                    concatAdapter.itemCount
                )
            }
        }
    }

    private fun showHealthRecordDeleteDialog(healthRecordItem: HealthRecordItem) {

        when (healthRecordItem.healthRecordType) {

            HealthRecordType.VACCINE_RECORD -> {
                AlertDialogHelper.showAlertDialog(
                    context = requireContext(),
                    title = getString(R.string.delete_hc_record_title),
                    msg = getString(R.string.delete_individual_vaccine_record_message),
                    positiveBtnMsg = getString(R.string.delete),
                    negativeBtnMsg = getString(R.string.not_now),
                    positiveBtnCallback = {
                        viewModel.deleteVaccineRecord(
                            healthRecordItem.patientId
                        )
                            .invokeOnCompletion {
                                viewModel.getIndividualsHealthRecord(
                                    args.patientId,
                                    filterSharedViewModel.filterState.value.timelineTypeFilter,
                                    filterSharedViewModel.filterState.value.filterFromDate,
                                    filterSharedViewModel.filterState.value.filterToDate
                                )
                            }
                    }
                )
            }

            HealthRecordType.COVID_TEST_RECORD -> {
                AlertDialogHelper.showAlertDialog(
                    context = requireContext(),
                    title = getString(R.string.delete_hc_record_title),
                    msg = getString(R.string.delete_individual_covid_test_record_message),
                    positiveBtnMsg = getString(R.string.delete),
                    negativeBtnMsg = getString(R.string.not_now),
                    positiveBtnCallback = {
                        viewModel.deleteTestRecord(
                            healthRecordItem.testResultId
                        )
                            .invokeOnCompletion {
                                viewModel.getIndividualsHealthRecord(
                                    args.patientId,
                                    filterSharedViewModel.filterState.value.timelineTypeFilter,
                                    filterSharedViewModel.filterState.value.filterFromDate,
                                    filterSharedViewModel.filterState.value.filterToDate
                                )
                            }
                    }
                )
            }
            else -> {
                // no implementation required
            }
        }
    }

    private fun queUser(value: String) {
        try {
            val uri = Uri.parse(URLDecoder.decode(value, StandardCharsets.UTF_8.name()))
            val customerId = uri.getQueryParameter("c")
            val waitingRoomId = uri.getQueryParameter("e")
            QueueService.IsTest = false
            val queueITEngine = QueueITEngine(
                requireActivity(),
                customerId,
                waitingRoomId,
                "",
                "",
                object : QueueListener() {
                    override fun onQueuePassed(queuePassedInfo: QueuePassedInfo?) {
                        viewModel.setQueItToken(queuePassedInfo?.queueItToken)
                    }

                    override fun onQueueViewWillOpen() {
                    }

                    override fun onQueueDisabled() {
                    }

                    override fun onQueueItUnavailable() {
                    }

                    override fun onError(error: Error?, errorMessage: String?) {
                    }
                }
            )
            queueITEngine.run(requireActivity())
        } catch (e: Exception) {
        }
    }

    private fun onBCSCLoginClick() {
        sharedViewModel.destinationId = 0
        findNavController().navigate(R.id.bcscAuthInfoFragment)
    }

    private fun protectiveWordFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener(
            KEY_MEDICATION_RECORD_REQUEST,
            viewLifecycleOwner
        ) { _, result ->
            viewModel.medicationRecordsUpdated(result.get(KEY_MEDICATION_RECORD_UPDATED) as Boolean)
        }
    }

    private fun observeCovidTestRecordAddition() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            FetchTestRecordFragment.TEST_RECORD_ADDED_SUCCESS
        )
            ?.observe(
                viewLifecycleOwner
            ) { recordId ->
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long>(
                    FetchTestRecordFragment.TEST_RECORD_ADDED_SUCCESS
                )
                if (recordId > 0) {
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(
                            HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
                            NavigationAction.ACTION_RE_CHECK
                        )
                    findNavController().popBackStack()
                }
            }
    }

    private fun observeVaccineRecordAddition() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS
        )?.observe(
            viewLifecycleOwner
        ) {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long>(
                FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS
            )
            if (it > 0) {
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(
                        HealthRecordPlaceholderFragment.PLACE_HOLDER_NAVIGATION,
                        NavigationAction.ACTION_RE_CHECK
                    )
                findNavController().popBackStack()
            }
        }
    }

    private fun observeFilterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                filterSharedViewModel.filterState.collect { filterState ->

                    // update filter date selection
                    if (isFilterDateSelected(filterState)) {
                        binding.chipDate.apply {
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
                        binding.chipDate.hide()
                    }

                    updateTypeFilterSelection(filterState)

                    updateClearButton(filterState)

                    viewModel.getIndividualsHealthRecord(
                        args.patientId,
                        filterSharedViewModel.filterState.value.timelineTypeFilter,
                        filterSharedViewModel.filterState.value.filterFromDate,
                        filterSharedViewModel.filterState.value.filterToDate
                    )
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
                TimelineTypeFilter.ALL
            )
        ) {
            binding.imgClear.hide()
        } else {
            binding.imgClear.show()
        }
    }
}
