package ca.bc.gov.bchealth.ui.healthrecords

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.databinding.FragmentIndividualHealthRecordBinding
import ca.bc.gov.bchealth.di.ApiClientModule
import ca.bc.gov.bchealth.http.MustBeQueued
import ca.bc.gov.bchealth.model.healthrecords.IndividualRecord
import ca.bc.gov.bchealth.model.healthrecords.toHealthRecord
import ca.bc.gov.bchealth.repository.HealthRecordType
import ca.bc.gov.bchealth.repository.Response
import ca.bc.gov.bchealth.utils.showAlertDialog
import ca.bc.gov.bchealth.utils.showError
import ca.bc.gov.bchealth.utils.viewBindings
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueITException
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class IndividualHealthRecordFragment : Fragment(R.layout.fragment_individual_health_record) {

    private val binding by viewBindings(FragmentIndividualHealthRecordBinding::bind)

    private val args: IndividualHealthRecordFragmentArgs by navArgs()

    private lateinit var individualHealthRecordAdapter: IndividualHealthRecordAdapter

    private val viewModel: IndividualHealthRecordViewModel by viewModels()

    private var updatePendingStatus = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        observeResponse()

        setUpRecyclerView()

        fetchUpdatedHealthRecords()
    }

    // Toolbar setup
    private fun setupToolBar() {

        binding.toolbar.ivLeftOption.visibility = View.VISIBLE
        binding.toolbar.ivLeftOption.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.tvTitle.visibility = View.VISIBLE
        binding.toolbar.tvTitle.isSelected = true
        binding.toolbar.tvTitle.text = args.healthRecord.name.plus(
            getString(R.string.member_records_toolbar_title)
        )

        binding.toolbar.tvRightOption.apply {
            visibility = View.VISIBLE
            text = getString(R.string.edit)
            setOnClickListener {
                if (individualHealthRecordAdapter.canDeleteRecord) {
                    text = getString(R.string.edit)
                    individualHealthRecordAdapter.canDeleteRecord = false
                } else {
                    text = getString(R.string.done)
                    individualHealthRecordAdapter.canDeleteRecord = true
                }
                individualHealthRecordAdapter.notifyItemRangeChanged(
                    0,
                    individualHealthRecordAdapter.itemCount
                )
            }
        }
    }

    private fun fetchUpdatedHealthRecords() {

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.healthRecords.collect { healthRecords ->

                    healthRecords?.let { individualHealthRecord ->

                        if (individualHealthRecord.isEmpty()) {
                            findNavController().popBackStack()
                        }

                        val records = individualHealthRecord.filter { individualRecord ->
                            individualRecord.name.lowercase() == args.healthRecord.name.lowercase()
                        }

                        if (records.isEmpty()) {
                            findNavController().popBackStack()
                        }
                        individualHealthRecordAdapter.individualRecords = records.toMutableList()
                        individualHealthRecordAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    // Recycler view setup
    private fun setUpRecyclerView() {

        individualHealthRecordAdapter = IndividualHealthRecordAdapter(
            mutableListOf(),
            false,
            onItemClickListener = { individualRecord ->

                when (individualRecord.healthRecordType) {

                    HealthRecordType.COVID_TEST_RECORD -> {
                        navigateToCovidTestResultPage(
                            individualRecord.covidTestResultList
                        )
                    }

                    HealthRecordType.VACCINE_RECORD -> {
                        val action = IndividualHealthRecordFragmentDirections
                            .actionIndividualHealthRecordFragmentToVaccineDetailsFragment(
                                individualRecord.toHealthRecord()
                            )
                        findNavController().navigate(action)
                    }
                }
            }, onDeleteListener = { individualRecord ->
            showHealthRecordDeleteDialog(individualRecord)
        }, updateListener = {
            requestUpdate(it)
        },
            updatePendingStatus
        )

        val recyclerView = binding.rvHealthRecords

        recyclerView.adapter = individualHealthRecordAdapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestUpdate(individualRecord: IndividualRecord) {

        updatePendingStatus = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.requestUpdate(individualRecord)
            } catch (e: Exception) {
                if (e is MustBeQueued) {
                    queueUser(e.getValue(), individualRecord)
                } else {
                    e.printStackTrace()
                    requireContext().showError(
                        getString(R.string.error),
                        getString(R.string.error_message)
                    )
                }
            }
        }
    }

    private fun showHealthRecordDeleteDialog(individualRecord: IndividualRecord) {

        when (individualRecord.healthRecordType) {

            HealthRecordType.COVID_TEST_RECORD -> {
                requireContext().showAlertDialog(
                    title = getString(R.string.delete_hc_record_title),
                    message = getString(R.string.delete_individual_covid_test_record_message),
                    positiveButtonText = getString(R.string.delete),
                    negativeButtonText = getString(R.string.not_now)
                ) {
                    viewModel.deleteCovidTestResult(
                        individualRecord.covidTestResultList.first()
                            .combinedReportId
                    )
                }
            }

            HealthRecordType.VACCINE_RECORD -> {
                requireContext().showAlertDialog(
                    title = getString(R.string.delete_hc_record_title),
                    message = getString(R.string.delete_individual_vaccine_record_message),
                    positiveButtonText = getString(R.string.delete),
                    negativeButtonText = getString(R.string.not_now)
                ) {
                    viewModel.deleteVaccineRecord(individualRecord.healthPassId)
                }
            }
        }
    }

    private fun navigateToCovidTestResultPage(covidTestResults: List<CovidTestResult>) {

        val action = IndividualHealthRecordFragmentDirections
            .actionIndividualHealthRecordFragmentToCovidTestResultFragment(
                covidTestResults.toTypedArray()
            )
        findNavController().navigate(action)
    }

    private fun observeResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseSharedFlow.collect {

                    when (it) {
                        is Response.Success -> {
                            respondToSuccess(this)
                        }
                        is Response.Error -> {
                            respondToError(it, this)
                        }
                        is Response.Loading -> {
                        }
                    }
                }
            }
        }
    }

    private fun respondToSuccess(
        coroutineScope: CoroutineScope
    ) {
        ApiClientModule.queueItToken = ""
        coroutineScope.cancel()
    }

    private fun respondToError(it: Response<String>, coroutineScope: CoroutineScope) {

        ApiClientModule.queueItToken = ""
        coroutineScope.cancel()
        requireContext().showError(
            it.errorData?.errorTitle.toString(),
            it.errorData?.errorMessage.toString()
        )
        individualHealthRecordAdapter.notifyDataSetChanged()
    }

    /*
    * HGS APIs are protected by Queue.it
    * User will see the Queue.it waiting page if there are more number of users trying to
    * access HGS service at the same time. Ref: https://github.com/queueit/android-webui-sdk
    * */
    private fun queueUser(value: String, individualRecord: IndividualRecord) {
        try {
            val valueUri = Uri.parse(URLDecoder.decode(value, StandardCharsets.UTF_8.name()))
            val customerId = valueUri.getQueryParameter("c")
            val waitingRoomId = valueUri.getQueryParameter("e")
            QueueService.IsTest = false
            val q = QueueITEngine(
                requireActivity(),
                customerId,
                waitingRoomId,
                "",
                "",
                object : QueueListener() {
                    override fun onQueuePassed(queuePassedInfo: QueuePassedInfo) {

                        ApiClientModule.queueItToken = queuePassedInfo.queueItToken

                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                viewModel.requestUpdate(individualRecord)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                requireContext().showError(
                                    getString(R.string.error),
                                    getString(R.string.error_message)
                                )
                            }
                        }
                    }

                    override fun onQueueViewWillOpen() {
                        Toast.makeText(
                            requireActivity(),
                            "Please wait! We are receiving more requests at the moment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onUserExited() {
                        // Not required
                    }

                    override fun onQueueDisabled() {
                        // Not required
                    }

                    override fun onQueueItUnavailable() {
                        requireContext().showError(
                            getString(R.string.error),
                            getString(R.string.error_message)
                        )
                    }

                    override fun onError(error: Error, errorMessage: String) {
                        requireContext().showError(
                            getString(R.string.error),
                            getString(R.string.error_message)
                        )
                    }

                    override fun onWebViewClosed() {
                        // Not required
                    }
                }
            )
            q.run(requireActivity())
        } catch (e: QueueITException) {
            e.printStackTrace()
            requireContext().showError(
                getString(R.string.error),
                getString(R.string.error_message)
            )
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            requireContext().showError(
                getString(R.string.error),
                getString(R.string.error_message)
            )
        }
    }
}
