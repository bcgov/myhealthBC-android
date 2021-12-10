package ca.bc.gov.bchealth.ui.healthrecords.covidtestresults

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import ca.bc.gov.bchealth.databinding.FragmentFetchCovidTestResultBinding
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.utils.Response
import ca.bc.gov.bchealth.utils.adjustOffset
import ca.bc.gov.bchealth.utils.isOnline
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showError
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FetchCovidTestResultFragment : Fragment(R.layout.fragment_fetch_covid_test_result) {

    private val binding by viewBindings(FragmentFetchCovidTestResultBinding::bind)

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)

    private val viewModel: FetchCovidTestResultViewModel by viewModels()

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
            tvTitle.text = getString(R.string.add_covid_test_result)

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

            /*binding.edPhnNumber.editText?.setText("9874307168")
            binding.edDob.editText?.setText("2014-03-15")
            binding.edDot.editText?.setText("2021-11-28")*/
        }

        setUpPhnUI()

        setUpDobUI()

        setUpDotUI()

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {

            if (validateInputData()) {

                observeResponse()

                viewLifecycleOwner.lifecycleScope.launch {

                    viewModel.getCovidTestResult(
                        binding.edPhnNumber.editText?.text.toString(),
                        binding.edDob.editText?.text.toString(),
                        binding.edDot.editText?.text.toString()
                    )
                }
            }
        }
    }

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
                                binding.edDot.editText?.requestFocus()
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

    private fun setUpDotUI() {
        val dateOfTestPicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        binding.edDot.editText?.setOnClickListener {
            dateOfTestPicker.show(parentFragmentManager, "DATE_OF_TEST")
        }
        binding.edDot.setEndIconOnClickListener {
            dateOfTestPicker.show(parentFragmentManager, "DATE_OF_TEST")
        }
        dateOfTestPicker.addOnPositiveButtonClickListener {
            binding.edDot.editText?.setText(simpleDateFormat.format(it.adjustOffset()))
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
        if (binding.edDot.editText?.text.isNullOrEmpty()) {
            binding.edDot.isErrorEnabled = true
            binding.edDot.error = getString(R.string.dot_required)
            binding.edDot.editText?.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isNotEmpty()) {
                    binding.edDot.isErrorEnabled = false
                    binding.edDot.error = null
                }
            }
            return false
        }

        if (!binding.edDot.editText?.text.toString()
            .matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) ||

            !binding.edDot.editText?.text.toString()
                .matches
                (Regex("^(\\d{4})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"))
        ) {
            binding.edDot.isErrorEnabled = true
            binding.edDot.error = getString(R.string.enter_valid_date_format)
            binding.edDot.editText?.doOnTextChanged { text, _, _, _ ->
                if (text != null && text.isNotEmpty()) {
                    binding.edDot.isErrorEnabled = false
                    binding.edDot.error = null
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

        showLoader(false)

        if (binding.checkboxRemember.isChecked) {
            // Save form data for autocomplete option
            val formData: String =
                binding.edPhnNumber.editText?.text.toString() +
                    binding.edDob.editText?.text.toString()

            viewModel.setRecentFormData(formData)
                .invokeOnCompletion {
                    navigateToIndividualRecords(response.data as String)
                    coroutineScope.cancel()
                }
        } else {
            navigateToIndividualRecords(response.data as String)
            coroutineScope.cancel()
        }
    }

    private fun respondToError(it: Response<String>, coroutineScope: CoroutineScope) {
        showLoader(false)
        requireContext().showError(
            it.errorData?.errorTitle.toString(),
            it.errorData?.errorMessage.toString()
        )
        coroutineScope.cancel()
    }

    private fun showLoader(value: Boolean) {
        if (value)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.INVISIBLE
    }

    private fun navigateToIndividualRecords(patientDisplayName: String) {

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.healthRecords.collect { healthRecords ->

                    healthRecords?.let {
                        navigate(it, patientDisplayName)
                    }
                }
            }
        }
    }

    private fun navigate(healthRecords: List<HealthRecord>, patientDisplayName: String) {

        var healthRecord: HealthRecord? = null
        healthRecords.forEach {
            if (it.name.lowercase() == patientDisplayName.lowercase())
                healthRecord = it
        }

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.fetchCovidTestResultFragment, true)
            .build()

        val action =
            healthRecord?.let {
                FetchCovidTestResultFragmentDirections
                    .actionFetchCovidTestResultFragmentToIndividualHealthRecordFragment(
                        it
                    )
            }

        action?.let { findNavController().navigate(it, navOptions) }
    }
}
