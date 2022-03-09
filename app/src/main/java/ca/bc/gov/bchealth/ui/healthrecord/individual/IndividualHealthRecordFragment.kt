package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.net.Uri
import android.os.Bundle
import android.view.View
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
import ca.bc.gov.bchealth.ui.login.BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME
import ca.bc.gov.bchealth.ui.login.BcscAuthFragment
import ca.bc.gov.bchealth.ui.login.BcscAuthState
import ca.bc.gov.bchealth.ui.login.BcscAuthViewModel
import ca.bc.gov.bchealth.ui.login.LoginSessionStatus
import ca.bc.gov.bchealth.utils.AlertDialogHelper
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
    private lateinit var hiddenHealthRecordAdapter: HiddenHealthRecordAdapter
    private lateinit var healthRecordsAdapter: HealthRecordsAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private val args: IndividualHealthRecordFragmentArgs by navArgs()
    private var testResultId: Long = -1L
    private val bcscAuthViewModel: BcscAuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var loginSessionStatus: LoginSessionStatus? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBcscAuthResponse()

        setupToolbar()

        setUpRecyclerView()

        if (loginSessionStatus == null) {
            observeBcscLogin()
            bcscAuthViewModel.checkLogin()
        } else {
            viewModel.getIndividualsHealthRecord(args.patientId)
        }

        setupObserver()
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
                    findNavController().popBackStack()
                }
                else -> {
                    // no implementation required}
                }
            }
        }
    }

    private fun getDummyData(authenticatedRecordsCount: Int): ArrayList<HiddenRecordItem> {
        return arrayListOf(HiddenRecordItem(authenticatedRecordsCount))
    }

    private fun observeHealthRecordsSyncCompletion() {
        val workRequest = WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(BACKGROUND_AUTH_RECORD_FETCH_WORK_NAME)
        if (!workRequest.hasObservers()) {
            workRequest.observe(viewLifecycleOwner) {
                if (it.firstOrNull()?.state == WorkInfo.State.ENQUEUED) {
                    viewModel.getIndividualsHealthRecord(args.patientId)
                }
            }
        }
    }

    private fun observeBcscLogin() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bcscAuthViewModel.authStatus.collect { authStatus ->
                    binding.progressBar.isVisible = authStatus.showLoading
                    if (authStatus.showLoading) {
                        return@collect
                    } else {
                        authStatus.loginSessionStatus?.let {
                            loginSessionStatus = it
                            viewModel.getIndividualsHealthRecord(args.patientId)
                        }
                    }
                }
            }
        }
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    if (loginSessionStatus != null) {
                        if (loginSessionStatus == LoginSessionStatus.ACTIVE) {
                            if (::healthRecordsAdapter.isInitialized) {
                                healthRecordsAdapter.submitList(uiState.onHealthRecords)
                            }
                        }

                        if (loginSessionStatus == LoginSessionStatus.EXPIRED) {
                            if (::healthRecordsAdapter.isInitialized) {
                                healthRecordsAdapter.submitList(uiState.onNonBcscHealthRecords)
                            }

                            if (uiState.authenticatedRecordsCount != null &&
                                uiState.patientAuthStatus == AuthenticationStatus.AUTHENTICATED &&
                                ::hiddenHealthRecordAdapter.isInitialized
                            ) {
                                hiddenHealthRecordAdapter.submitList(
                                    getDummyData(uiState.authenticatedRecordsCount)
                                )
                            }
                        }

                        if (uiState.patientAuthStatus == AuthenticationStatus.AUTHENTICATED) {
                            observeHealthRecordsSyncCompletion()
                        }
                    }

                    if (uiState.patientAuthStatus == AuthenticationStatus.AUTHENTICATED) {
                        binding.toolbar.tvRightOption.visibility = View.INVISIBLE
                        healthRecordsAdapter.isUpdateRequested = false
                    }

                    if (uiState.updatedTestResultId > 0) {
                        viewModel.getIndividualsHealthRecord(args.patientId)
                        return@collect
                    }

                    if (uiState.queItTokenUpdated) {
                        requestUpdate(testResultId)
                    }

                    if (uiState.onMustBeQueued && uiState.queItUrl != null) {
                        queUser(uiState.queItUrl)
                    }
                }
            }
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
                        val action = IndividualHealthRecordFragmentDirections
                            .actionIndividualHealthRecordFragmentToTestResultDetailFragment(
                                it.patientId,
                                it.testResultId
                            )
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
        concatAdapter = ConcatAdapter(
            hiddenHealthRecordAdapter,
            healthRecordsAdapter
        )
        binding.rvHealthRecords.adapter = concatAdapter
        binding.rvHealthRecords.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestUpdate(testResultId: Long) {
        this.testResultId = testResultId
        viewModel.requestUpdate(testResultId)
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = args.patientName.plus("'s record")
            line1.visibility = View.VISIBLE

            binding.toolbar.tvRightOption.apply {
                visibility = View.VISIBLE
                text = getString(R.string.edit)
                setOnClickListener {
                    if (healthRecordsAdapter.canDeleteRecord) {
                        text = getString(R.string.edit)
                        healthRecordsAdapter.canDeleteRecord = false
                    } else {
                        text = getString(R.string.done)
                        healthRecordsAdapter.canDeleteRecord = true
                    }

                    concatAdapter.notifyItemRangeChanged(
                        0,
                        concatAdapter.itemCount
                    )
                }
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
                            .invokeOnCompletion { viewModel.getIndividualsHealthRecord(args.patientId) }
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
                            .invokeOnCompletion { viewModel.getIndividualsHealthRecord(args.patientId) }
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
}
