package ca.bc.gov.bchealth.ui.healthrecords.vaccinerecords

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.analytics.AnalyticsAction
import ca.bc.gov.bchealth.analytics.AnalyticsText
import ca.bc.gov.bchealth.analytics.SelfDescribingEvent
import ca.bc.gov.bchealth.data.local.entity.HealthCard
import ca.bc.gov.bchealth.databinding.FragmentFetchVaccineRecordBinding
import ca.bc.gov.bchealth.di.ApiClientModule
import ca.bc.gov.bchealth.http.MustBeQueued
import ca.bc.gov.bchealth.model.ImmunizationRecord
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.adjustOffset
import ca.bc.gov.bchealth.utils.isOnline
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showCardReplacementDialog
import ca.bc.gov.bchealth.utils.showError
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.FetchVaccineDataViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueITException
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import com.snowplowanalytics.snowplow.Snowplow
import dagger.hilt.android.AndroidEntryPoint
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class FetchVaccineRecordFragment : Fragment(R.layout.fragment_fetch_vaccine_record) {

    private val binding by viewBindings(FragmentFetchVaccineRecordBinding::bind)

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)

    private val viewModel: FetchVaccineDataViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        iniUI()
    }

    private fun setToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.add_bc_vaccine_record)

            ivRightOption.visibility = View.VISIBLE
            ivRightOption.setImageResource(R.drawable.ic_help)
            ivRightOption.setOnClickListener {
                requireActivity().redirect(getString(R.string.url_help))
            }
            ivRightOption.contentDescription = getString(R.string.help)

            line1.visibility = View.VISIBLE
        }
    }

    private fun iniUI() {

        if (BuildConfig.DEBUG) {
            /*binding.edPhnNumber.editText?.setText("9000201422")
            binding.edDob.editText?.setText("1989-12-12")
            binding.edDov.editText?.setText("2021-05-15")*/

            /*binding.edPhnNumber.editText?.setText("9000691304")
            binding.edDob.editText?.setText("1965-01-14")
            binding.edDov.editText?.setText("2021-07-15")*/

            /*binding.edPhnNumber.editText?.setText("9890826056")
            binding.edDob.editText?.setText("1962-01-02")
            binding.edDov.editText?.setText("2021-06-10")*/

            /*binding.edPhnNumber.editText?.setText("9879458314")
            binding.edDob.editText?.setText("1934-02-23")
            binding.edDov.editText?.setText("2021-04-26")*/
        }

        setUpPhnUI()

        setUpDobUI()

        setUpDovUI()

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {

            if (validateInputData()) {

                observeResponse()

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        viewModel.getVaccineStatus(
                            binding.edPhnNumber.editText?.text.toString(),
                            binding.edDob.editText?.text.toString(),
                            binding.edDov.editText?.text.toString()
                        )
                    } catch (e: Exception) {
                        if (e is MustBeQueued) {
                            withContext(Dispatchers.Main) {
                                queueUser(e.getValue())
                            }
                        } else {
                            e.printStackTrace()
                            showLoader(false)
                            requireContext().showError(
                                getString(R.string.error),
                                getString(R.string.error_message)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun validateInputData(): Boolean {

        if (validatePhnNumber()) {

            if (validateDob()) {

                if (validateDov()) {

                    if (!requireContext().isOnline()) {
                        requireContext().showError(
                            getString(R.string.no_internet),
                            getString(R.string.check_connection)
                        )
                        return false
                    }
                } else {
                    return false
                }
            } else {
                return false
            }
        } else {
            return false
        }

        return true
    }

    private fun validatePhnNumber(): Boolean {
        if (binding.edPhnNumber.editText?.text.isNullOrEmpty()) {
            binding.edPhnNumber.isErrorEnabled = true
            binding.edPhnNumber.error = getString(R.string.phn_number_required)
            binding.edPhnNumber.editText?.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isNotEmpty()) {
                    binding.edPhnNumber.isErrorEnabled = false
                    binding.edPhnNumber.error = null
                }
            }
            return false
        }

        if (binding.edPhnNumber.editText?.text?.length != 10) {
            binding.edPhnNumber.isErrorEnabled = true
            binding.edPhnNumber.error = getString(R.string.phn_should_be_10_digit)
            binding.edPhnNumber.editText?.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isNotEmpty()) {
                    binding.edPhnNumber.isErrorEnabled = false
                    binding.edPhnNumber.error = null
                }
            }
            return false
        }

        return true
    }

    private fun validateDob(): Boolean {
        if (binding.edDob.editText?.text.isNullOrEmpty()) {
            binding.edDob.isErrorEnabled = true
            binding.edDob.error = getString(R.string.dob_required)
            binding.edDob.editText?.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isNotEmpty()) {
                    binding.edDob.isErrorEnabled = false
                    binding.edDob.error = null
                }
            }
            return false
        }

        if (!binding.edDob.editText?.text.toString()
            .matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) ||

            !binding.edDob.editText?.text.toString()
                .matches(Regex("^(\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"))
        ) {
            binding.edDob.isErrorEnabled = true
            binding.edDob.error = getString(R.string.enter_valid_date_format)
            binding.edDob.editText?.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isNotEmpty()) {
                    binding.edDob.isErrorEnabled = false
                    binding.edDob.error = null
                }
            }
            return false
        }

        return true
    }

    private fun validateDov(): Boolean {
        if (binding.edDov.editText?.text.isNullOrEmpty()) {
            binding.edDov.isErrorEnabled = true
            binding.edDov.error = getString(R.string.dov_required)
            binding.edDov.editText?.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isNotEmpty()) {
                    binding.edDov.isErrorEnabled = false
                    binding.edDov.error = null
                }
            }
            return false
        }

        if (!binding.edDov.editText?.text.toString()
            .matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) ||

            !binding.edDov.editText?.text.toString()
                .matches
                (Regex("^(\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"))
        ) {
            binding.edDov.isErrorEnabled = true
            binding.edDov.error = getString(R.string.enter_valid_date_format)
            binding.edDov.editText?.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isNotEmpty()) {
                    binding.edDov.isErrorEnabled = false
                    binding.edDov.error = null
                }
            }
            return false
        }

        return true
    }

    private fun observeResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.responseSharedFlow.collect {
                    when (it) {
                        is Response.Success -> {
                            respondToSuccess(it, this)
                        }
                        is Response.Error -> {
                            respondToError(it, this)
                        }
                        is Response.Loading -> {
                            showLoader(true)
                        }
                    }
                }
            }
        }
    }

    private fun respondToSuccess(
        response: Response.Success<String>,
        coroutineScope: CoroutineScope
    ) {
        ApiClientModule.queueItToken = ""

        showLoader(false)

        if (binding.checkboxRemember.isChecked) {
            // Save form data for autocomplete option
            val formData: String =
                binding.edPhnNumber.editText?.text.toString() +
                    binding.edDob.editText?.text.toString()

            viewModel.setRecentFormData(formData)
                .invokeOnCompletion {

                    val pair = response.data as Pair<*, *>
                    if (pair.second as Boolean) {
                        showCardReplacement(pair.first as HealthCard)
                    } else {
                        navigateToIndividualRecords(pair.first as HealthCard)
                    }

                    coroutineScope.cancel()
                }
        } else {

            val pair = response.data as Pair<*, *>
            if (pair.second as Boolean) {
                showCardReplacement(pair.first as HealthCard)
            } else {
                navigateToIndividualRecords(pair.first as HealthCard)
            }

            coroutineScope.cancel()
        }
    }

    private fun showLoader(value: Boolean) {
        if (value)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.INVISIBLE
    }

    private fun respondToError(it: Response<String>, coroutineScope: CoroutineScope) {

        ApiClientModule.queueItToken = ""
        showLoader(false)
        requireContext().showError(
            it.errorData?.errorTitle.toString(),
            it.errorData?.errorMessage.toString()
        )
        coroutineScope.cancel()
    }

    /*
     * Fetch saved form data
     * */
    private fun setUpPhnUI() {

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRecentFormData.collect {
                    if (it.isNotEmpty()) {

                        val pair = Pair(
                            it.subSequence(0, 10),
                            it.subSequence(10, 20)
                        )

                        val phnArray = arrayOf(pair.first.toString())

                        val adapter: ArrayAdapter<String> = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            phnArray
                        )

                        val textView = binding.edPhnNumber.editText as AutoCompleteTextView
                        textView.setAdapter(adapter)
                        textView.onItemClickListener =
                            AdapterView.OnItemClickListener { _, _, _, _ ->
                                binding.edDob.editText?.setText(pair.second.toString())
                                binding.edDov.editText?.requestFocus()
                            }

                        binding.edPhnNumber.setEndIconDrawable(R.drawable.ic_arrow_down)
                        binding.edPhnNumber.setEndIconOnClickListener {
                            textView.showDropDown()
                        }
                    }
                }
            }
        }
    }

    private fun setUpDobUI() {
        val dateOfBirthPicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        binding.edDob.editText?.setOnClickListener {
            dateOfBirthPicker.show(parentFragmentManager, "DATE_OF_BIRTH")
        }
        binding.edDob.setEndIconOnClickListener {
            dateOfBirthPicker.show(parentFragmentManager, "DATE_OF_BIRTH")
        }
        dateOfBirthPicker.addOnPositiveButtonClickListener {
            binding.edDob.editText
                ?.setText(simpleDateFormat.format(it.adjustOffset()))
        }
    }

    private fun setUpDovUI() {
        val dateOfVaccinationPicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        binding.edDov.editText?.setOnClickListener {
            dateOfVaccinationPicker.show(parentFragmentManager, "DATE_OF_VACCINATION")
        }
        binding.edDov.setEndIconOnClickListener {
            dateOfVaccinationPicker.show(parentFragmentManager, "DATE_OF_VACCINATION")
        }
        dateOfVaccinationPicker.addOnPositiveButtonClickListener {
            binding.edDov.editText?.setText(simpleDateFormat.format(it.adjustOffset()))
        }
    }

    /*
    * HGS APIs are protected by Queue.it
    * User will see the Queue.it waiting page if there are more number of users trying to
    * access HGS service at the same time. Ref: https://github.com/queueit/android-webui-sdk
    * */
    private fun queueUser(value: String) {
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
                                viewModel.getVaccineStatus(
                                    binding.edPhnNumber.editText?.text.toString(),
                                    binding.edDob.editText?.text.toString(),
                                    binding.edDov.editText?.text.toString()
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                showLoader(false)
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

    private fun showCardReplacement(healthCard: HealthCard) {
        requireContext().showCardReplacementDialog {
            viewModel.replaceExitingHealthPass(healthCard).invokeOnCompletion {
                navigateToIndividualRecords(healthCard)
            }
        }
    }

    private fun navigateToIndividualRecords(healthCard: HealthCard) {

        // Snowplow event
        Snowplow.getDefaultTracker()?.track(
            SelfDescribingEvent
                .get(AnalyticsAction.AddQR.value, AnalyticsText.Get.value)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.healthRecords.collect { healthRecords ->

                    healthRecords?.let {
                        viewModel.fetchHealthRecordFromHealthCard(healthCard)?.let { immuRecord ->

                            navigate(healthRecords, immuRecord)
                        }
                    }
                }
            }
        }
    }

    private fun navigate(healthRecords: List<HealthRecord>, immuRecord: ImmunizationRecord) {

        var healthRecord: HealthRecord? = null
        healthRecords.forEach {
            if (it.name.lowercase() == immuRecord.name.lowercase())
                healthRecord = it
        }

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.addHealthRecordsFragment, true)
            .build()

        val action = healthRecord?.let {
            FetchVaccineRecordFragmentDirections
                .actionFetchVaccineRecordFragmentToIndividualHealthRecordFragment(
                    it
                )
        }

        action?.let { findNavController().navigate(it, navOptions) }
    }
}
