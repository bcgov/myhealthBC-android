package ca.bc.gov.bchealth.ui.travelpass

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
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.analytics.AnalyticsAction
import ca.bc.gov.bchealth.analytics.AnalyticsText
import ca.bc.gov.bchealth.analytics.SelfDescribingEvent
import ca.bc.gov.bchealth.databinding.FragmentFetchTravelPassBinding
import ca.bc.gov.bchealth.ui.healthpass.add.AddOrUpdateCardViewModel
import ca.bc.gov.bchealth.ui.healthpass.add.FetchVaccineRecordViewModel
import ca.bc.gov.bchealth.ui.healthpass.add.Status
import ca.bc.gov.bchealth.utils.showAlertDialog
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.relation.PatientAndVaccineRecord
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.repository.model.PatientVaccineRecord
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class FetchFederalTravelPassFragment : Fragment(R.layout.fragment_fetch_travel_pass) {
    private val binding by viewBindings(FragmentFetchTravelPassBinding::bind)
    private val viewModel: FetchVaccineRecordViewModel by viewModels()
    private val args: FetchFederalTravelPassFragmentArgs by navArgs()
    private val addOrUpdateCardViewModel: AddOrUpdateCardViewModel by viewModels()
    private lateinit var patientData: PatientAndVaccineRecord

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.get_federal_travel_pass)
            line1.visibility = View.VISIBLE
        }

        binding.btnSubmit.setOnClickListener {
            fetchTravelPass()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.queItTokenUpdated) {
                        fetchTravelPass()
                    }

                    if (uiState.onMustBeQueued && uiState.queItUrl != null) {

                        queUser(uiState.queItUrl)
                    }

                    if (uiState.vaccineRecord != null) {
                        addOrUpdateCardViewModel.processResult(uiState.vaccineRecord)
                    }

                    if (uiState.patientData != null) {
                        patientData = uiState.patientData
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                addOrUpdateCardViewModel.uiState.collect { state ->
                    if (state.vaccineRecord != null) {
                        performActionBasedOnState(state.state, state.vaccineRecord!!)
                    }
                }
            }
        }

        viewModel.getPatientWithVaccineRecord(args.patientId)
    }

    private fun fetchTravelPass() {
        val phn = binding.edPhn.text.toString()
        if (phn.isBlank()) {
            binding.edPhnNumber.requestFocus()
            binding.edPhnNumber.error = "Invalid PHN"
        } else {
            // viewModel.fetchVaccineRecord("9000691304", "1965-01-14", "2021-07-15")
            viewModel.fetchVaccineRecord(
                phn,
                patientData.patient.dateOfBirth.toDate(),
                patientData.vaccineRecord?.doses!!.last().date.toDate()
            )
        }
    }

    private fun performActionBasedOnState(state: Status, record: PatientVaccineRecord) =
        when (state) {

            Status.CAN_INSERT,
            Status.DUPLICATE,
            Status.CAN_UPDATE -> {
                updateRecord(record)
            }
            Status.INSERTED,
            Status.UPDATED -> {
                navigateToCardsList()
            }
            else -> {}
        }

    private fun updateRecord(vaccineRecord: PatientVaccineRecord) {
        requireContext().showAlertDialog(
            title = getString(R.string.replace_health_pass_title),
            message = getString(R.string.replace_health_pass_message),
            positiveButtonText = getString(R.string.replace),
            negativeButtonText = getString(R.string.not_now)
        ) {
            addOrUpdateCardViewModel.update(vaccineRecord)
        }
    }

    private fun insert(vaccineRecord: PatientVaccineRecord) {
        addOrUpdateCardViewModel.insert(vaccineRecord)
    }

    private fun navigateToCardsList() {
        // Snowplow event
        Snowplow.getDefaultTracker()?.track(
            SelfDescribingEvent
                .get(
                    AnalyticsAction.AddQR.value,
                    AnalyticsText.Upload.value
                )
        )
        findNavController().popBackStack()
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
}
