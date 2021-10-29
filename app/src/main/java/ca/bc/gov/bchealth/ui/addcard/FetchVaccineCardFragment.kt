package ca.bc.gov.bchealth.ui.addcard

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.BuildConfig
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentFetchVaccineCardBinding
import ca.bc.gov.bchealth.di.ApiClientModule
import ca.bc.gov.bchealth.http.MustBeQueued
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.adjustOffset
import ca.bc.gov.bchealth.utils.isOnline
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.queue_it.androidsdk.Error
import com.queue_it.androidsdk.QueueITEngine
import com.queue_it.androidsdk.QueueITException
import com.queue_it.androidsdk.QueueListener
import com.queue_it.androidsdk.QueuePassedInfo
import com.queue_it.androidsdk.QueueService
import dagger.hilt.android.AndroidEntryPoint
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class FetchVaccineCardFragment : Fragment(R.layout.fragment_fetch_vaccine_card) {

    private val binding by viewBindings(FragmentFetchVaccineCardBinding::bind)

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)

    private val viewModel: FetchVaccineCardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        iniUI()
    }

    private fun setToolBar() {
        binding.toolbar.apply {
            ivBack.visibility = View.VISIBLE
            ivBack.setImageResource(R.drawable.ic_action_back)
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.add_bc_vaccine_card)

            ivSettings.visibility = View.VISIBLE
            ivSettings.setImageResource(R.drawable.ic_help)
            ivSettings.setOnClickListener {
                requireActivity().redirect(getString(R.string.url_help))
            }

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
        }

        setUpDobUI()

        setUpDovUI()

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            if (validateInputData()) {

                viewLifecycleOwner.lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.responseSharedFlow.collect {
                            when (it) {
                                is Response.Success -> {
                                    ApiClientModule.queueItToken = ""
                                    binding.progressBar.visibility = View.INVISIBLE
                                    findNavController().popBackStack(R.id.myCardsFragment, false)
                                    this.cancel()
                                }
                                is Response.Error -> {
                                    ApiClientModule.queueItToken = ""
                                    binding.progressBar.visibility = View.INVISIBLE
                                    showError(
                                        it.errorData?.errorTitle.toString(),
                                        it.errorData?.errorMessage.toString()
                                    )
                                    this.cancel()
                                }
                                is Response.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }

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
                            showError(
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

        if (binding.edPhnNumber.editText?.text.isNullOrEmpty()) {
            binding.edPhnNumber.isErrorEnabled = true
            binding.edPhnNumber.error = "Personal Health Number is required"
            binding.edPhnNumber.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edPhnNumber.isErrorEnabled = false
                        binding.edPhnNumber.error = null
                    }
            }
            return false
        }

        if (binding.edPhnNumber.editText?.text?.length != 10) {
            binding.edPhnNumber.isErrorEnabled = true
            binding.edPhnNumber.error = "PHN should be 10 characters"
            binding.edPhnNumber.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edPhnNumber.isErrorEnabled = false
                        binding.edPhnNumber.error = null
                    }
            }
            return false
        }

        if (binding.edDob.editText?.text.isNullOrEmpty()) {
            binding.edDob.isErrorEnabled = true
            binding.edDob.error = "Date of Birth is required"
            binding.edDob.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edDob.isErrorEnabled = false
                        binding.edDob.error = null
                    }
            }
            return false
        }

        if (!binding.edDob.editText?.text.toString()
            .matches(Regex("^(\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"))
        ) {
            binding.edDob.isErrorEnabled = true
            binding.edDob.error = "Please enter a valid date format"
            binding.edDob.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edDob.isErrorEnabled = false
                        binding.edDob.error = null
                    }
            }
            return false
        }

        if (binding.edDov.editText?.text.isNullOrEmpty()) {
            binding.edDov.isErrorEnabled = true
            binding.edDov.error = "Date of Vaccination is required"
            binding.edDov.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edDov.isErrorEnabled = false
                        binding.edDov.error = null
                    }
            }
            return false
        }

        if (!binding.edDov.editText?.text.toString()
            .matches(Regex("^(\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"))
        ) {
            binding.edDov.isErrorEnabled = true
            binding.edDov.error = "Please enter a valid date format"
            binding.edDov.editText?.doOnTextChanged { text, start, before, count ->
                if (text != null)
                    if (text.isNotEmpty()) {
                        binding.edDov.isErrorEnabled = false
                        binding.edDov.error = null
                    }
            }
            return false
        }

        if (!requireContext().isOnline()) {
            showError(
                getString(R.string.no_internet),
                getString(R.string.check_connection)
            )
            return false
        }

        return true
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
                                showError(
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
                    }

                    override fun onQueueDisabled() {
                    }

                    override fun onQueueItUnavailable() {
                        showError(
                            getString(R.string.error),
                            getString(R.string.error_message)
                        )
                    }

                    override fun onError(error: Error, errorMessage: String) {
                        showError(
                            getString(R.string.error),
                            getString(R.string.error_message)
                        )
                    }

                    override fun onWebViewClosed() {
                    }
                }
            )
            q.run(requireActivity())
        } catch (e: QueueITException) {
            e.printStackTrace()
            showError(
                getString(R.string.error),
                getString(R.string.error_message)
            )
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            showError(
                getString(R.string.error),
                getString(R.string.error_message)
            )
        }
    }

    private fun showError(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setCancelable(false)
            .setMessage(message)
            .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
