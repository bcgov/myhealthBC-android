package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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
import ca.bc.gov.bchealth.utils.showAlertDialog
import ca.bc.gov.bchealth.utils.viewBindings
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
    private lateinit var concatAdapter: ConcatAdapter
    private val args: IndividualHealthRecordFragmentArgs by navArgs()
    private var patientId: Long = -1L
    private var testResultId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        setUpRecyclerView()

        setupObserver()

        viewModel.getIndividualsHealthRecord(args.patientId)

        hiddenHealthRecordAdapter.submitList(getDummyData())
    }

    private fun getDummyData(): ArrayList<HiddenRecordItem> {
        return arrayListOf(HiddenRecordItem(1))
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    if (::vaccineRecordsAdapter.isInitialized) {
                        vaccineRecordsAdapter.submitList(uiState.onVaccineRecord)
                    }

                    if (::testRecordsAdapter.isInitialized) {
                        testRecordsAdapter.submitList(uiState.onTestRecords)
                    }

                    if (uiState.updatedTestResultId > 0) {
                        viewModel.getIndividualsHealthRecord(args.patientId)
                        return@collect
                    }

                    if (uiState.queItTokenUpdated) {
                        requestUpdate(patientId, testResultId)
                    }

                    if (uiState.onTestRecords.isEmpty() && uiState.onVaccineRecord.isEmpty())
                        findNavController().popBackStack()

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
                requestUpdate(patientId, testResult)
            },
            isUpdateRequested = true,
            canDeleteRecord = false
        )

        hiddenHealthRecordAdapter = HiddenHealthRecordAdapter { onBCSCLoginClick() }
        concatAdapter = ConcatAdapter(hiddenHealthRecordAdapter, vaccineRecordsAdapter, testRecordsAdapter)
        binding.rvHealthRecords.adapter = concatAdapter
        binding.rvHealthRecords.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestUpdate(patientId: Long, testResultId: Long) {
        this.patientId = patientId
        this.testResultId = testResultId
        viewModel.requestUpdate(patientId, testResultId)
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
                requireContext().showAlertDialog(
                    title = getString(R.string.delete_hc_record_title),
                    message = getString(R.string.delete_individual_vaccine_record_message),
                    positiveButtonText = getString(R.string.delete),
                    negativeButtonText = getString(R.string.not_now)
                ) {
                    viewModel.deleteVaccineRecord(
                        healthRecordItem.patientId
                    ).invokeOnCompletion { viewModel.getIndividualsHealthRecord(args.patientId) }
                }
            }

            HealthRecordType.COVID_TEST_RECORD -> {
                requireContext().showAlertDialog(
                    title = getString(R.string.delete_hc_record_title),
                    message = getString(R.string.delete_individual_covid_test_record_message),
                    positiveButtonText = getString(R.string.delete),
                    negativeButtonText = getString(R.string.not_now)
                ) {
                    viewModel.deleteTestRecord(
                        healthRecordItem.testResultId
                    ).invokeOnCompletion { viewModel.getIndividualsHealthRecord(args.patientId) }
                }
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
        // Do nothing
    }
}
