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
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentIndividualHealthRecordBinding
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
    private lateinit var vaccineRecordsAdapter: VaccineRecordsAdapter
    private lateinit var testRecordsAdapter: TestRecordsAdapter
    private lateinit var hiddenHealthRecordAdapter: HiddenHealthRecordAdapter
    private lateinit var medicationRecordsAdapter: MedicationRecordsAdapter
    private lateinit var labTestRecordsAdapter: LabTestRecordsAdapter
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

    private fun observeBcscLogin() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bcscAuthViewModel.authStatus.collect {
                    binding.progressBar.isVisible = it.showLoading
                    if (it.showLoading) {
                        return@collect
                    } else {
                        it.loginSessionStatus?.let {
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
                            if (::vaccineRecordsAdapter.isInitialized) {
                                vaccineRecordsAdapter.submitList(uiState.onVaccineRecord)
                            }

                            if (::testRecordsAdapter.isInitialized) {
                                testRecordsAdapter.submitList(uiState.onTestRecords)
                            }

                            if (::medicationRecordsAdapter.isInitialized) {
                                medicationRecordsAdapter.submitList(uiState.onMedicationRecords)
                            }

                            if (::labTestRecordsAdapter.isInitialized) {
                                labTestRecordsAdapter.submitList(uiState.onLabTestRecords)
                            }
                        }

                        if (loginSessionStatus == LoginSessionStatus.EXPIRED) {
                            if (::vaccineRecordsAdapter.isInitialized) {
                                vaccineRecordsAdapter.submitList(uiState.onNonBcscVaccineRecord)
                            }

                            if (::testRecordsAdapter.isInitialized) {
                                testRecordsAdapter.submitList(uiState.onNonBcscTestRecords)
                            }

                            if (::medicationRecordsAdapter.isInitialized) {
                                medicationRecordsAdapter.submitList(uiState.onNonBcscMedicationRecords)
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
                    }

                    if (uiState.patientAuthStatus == AuthenticationStatus.AUTHENTICATED) {
                        binding.toolbar.tvRightOption.visibility = View.INVISIBLE
                        testRecordsAdapter.isUpdateRequested = false
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
        vaccineRecordsAdapter = VaccineRecordsAdapter(
            { vaccineRecord ->
                val action = IndividualHealthRecordFragmentDirections
                    .actionIndividualHealthRecordFragmentToVaccineRecordDetailFragment(
                        vaccineRecord.patientId
                    )
                findNavController().navigate(action)
            },
            { vaccineRecord ->
                showHealthRecordDeleteDialog(vaccineRecord)
            }
        )

        testRecordsAdapter = TestRecordsAdapter(
            { testResult ->
                val action = IndividualHealthRecordFragmentDirections
                    .actionIndividualHealthRecordFragmentToTestResultDetailFragment(
                        testResult.patientId,
                        testResult.testResultId
                    )
                findNavController().navigate(action)
            },
            { testResult ->
                showHealthRecordDeleteDialog(testResult)
            },
            { patientId, testResult ->
                requestUpdate(testResult)
            },
            isUpdateRequested = true,
            canDeleteRecord = false
        )

        medicationRecordsAdapter = MedicationRecordsAdapter { medicationRecord ->
            val action = IndividualHealthRecordFragmentDirections
                .actionIndividualHealthRecordFragmentToMedicationDetailFragment(
                    medicationRecord.medicationRecordId
                )
            findNavController().navigate(action)
        }

        labTestRecordsAdapter = LabTestRecordsAdapter { labTestRecord ->
            val action = IndividualHealthRecordFragmentDirections
                .actionIndividualHealthRecordFragmentToLabTestDetailFragment(
                    labTestRecord.patientId
                )
            findNavController().navigate(action)
        }

        hiddenHealthRecordAdapter = HiddenHealthRecordAdapter { onBCSCLoginClick() }
        concatAdapter = ConcatAdapter(
            hiddenHealthRecordAdapter,
            vaccineRecordsAdapter,
            testRecordsAdapter,
            medicationRecordsAdapter,
            labTestRecordsAdapter
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
                    if (vaccineRecordsAdapter.canDeleteRecord) {
                        text = getString(R.string.edit)
                        vaccineRecordsAdapter.canDeleteRecord = false
                    } else {
                        text = getString(R.string.done)
                        vaccineRecordsAdapter.canDeleteRecord = true
                    }

                    if (testRecordsAdapter.canDeleteRecord) {
                        text = getString(R.string.edit)
                        testRecordsAdapter.canDeleteRecord = false
                    } else {
                        text = getString(R.string.done)
                        testRecordsAdapter.canDeleteRecord = true
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
