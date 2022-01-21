package ca.bc.gov.bchealth.ui.travelpass

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFetchTravelPassBinding
import ca.bc.gov.bchealth.ui.custom.validatePhnNumber
import ca.bc.gov.bchealth.ui.healthpass.add.AddOrUpdateCardViewModel
import ca.bc.gov.bchealth.ui.healthpass.add.FetchVaccineRecordViewModel
import ca.bc.gov.bchealth.ui.healthpass.add.Status
import ca.bc.gov.bchealth.utils.showError
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.bchealth.viewmodel.RecentPhnDobViewModel
import ca.bc.gov.common.model.analytics.AnalyticsAction
import ca.bc.gov.common.model.analytics.AnalyticsActionData
import ca.bc.gov.common.model.relation.PatientAndVaccineRecord
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.yyyy_MM_dd
import ca.bc.gov.repository.model.PatientVaccineRecord
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
class FetchFederalTravelPassFragment : Fragment(R.layout.fragment_fetch_travel_pass) {
    private val binding by viewBindings(FragmentFetchTravelPassBinding::bind)
    private val viewModel: FetchVaccineRecordViewModel by viewModels()
    private val args: FetchFederalTravelPassFragmentArgs by navArgs()
    private val addOrUpdateCardViewModel: AddOrUpdateCardViewModel by viewModels()
    private lateinit var patientData: PatientAndVaccineRecord
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()
    private val recentPhnDobViewModel: RecentPhnDobViewModel by viewModels()

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

        setUpPhnUI()

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    showLoader(uiState.onLoading)

                    if (uiState.errorData != null) {
                        requireContext().showError(
                            getString(uiState.errorData.title),
                            getString(uiState.errorData.message)
                        )
                    }

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

                    showLoader(state.onLoading)

                    performActionBasedOnState(state.state, state.vaccineRecord)
                }
            }
        }

        viewModel.getPatientWithVaccineRecord(args.patientId)
    }

    private fun setUpPhnUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                recentPhnDobViewModel.recentPhnDob.collect { recentPhnDob ->
                    val (phn, dob) = recentPhnDob
                    val phnArray = arrayOf(phn)

                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        phnArray
                    )

                    val textView = binding.edPhnNumber.editText as AutoCompleteTextView
                    textView.setAdapter(adapter)
                    binding.edPhnNumber.setEndIconDrawable(R.drawable.ic_arrow_down)
                    binding.edPhnNumber.setEndIconOnClickListener {
                        textView.showDropDown()
                    }
                }
            }
        }
    }

    private fun showLoader(value: Boolean) {
        binding.btnSubmit.isEnabled = !value
        binding.progressBar.isVisible = value
    }

    private fun fetchTravelPass() {
        val phn = binding.edPhn.text.toString()
        if (this.validatePhnNumber(binding.edPhnNumber, "Invalid PHN")) {
            viewModel.fetchVaccineRecord(
                phn,
                patientData.patientDto.dateOfBirth.toDate(yyyy_MM_dd),
                patientData.vaccineRecordDto?.doseDtos!!.last().date.toDate(yyyy_MM_dd)
            )
        }
    }

    private fun performActionBasedOnState(state: Status, record: PatientVaccineRecord?) =
        when (state) {
            Status.CAN_INSERT,
            Status.DUPLICATE,
            Status.CAN_UPDATE -> {
                record?.let { updateRecord(it) }
            }
            Status.INSERTED,
            Status.UPDATED -> {
                record?.vaccineRecordDto?.federalPass?.let { showTravelPass(it) }
            }
            else -> {
            }
        }

    private fun updateRecord(vaccineRecord: PatientVaccineRecord) {
        addOrUpdateCardViewModel.update(vaccineRecord)
    }

    private fun showTravelPass(federalPass: String) {
        // Snowplow event
        analyticsFeatureViewModel.track(AnalyticsAction.ADD_QR, AnalyticsActionData.GET)
        val action = FetchFederalTravelPassFragmentDirections
            .actionFetchFederalTravelPassToTravelPassFragment(federalPass)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.fetchFederalTravelPass, true)
            .build()

        findNavController().navigate(action, navOptions)
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
