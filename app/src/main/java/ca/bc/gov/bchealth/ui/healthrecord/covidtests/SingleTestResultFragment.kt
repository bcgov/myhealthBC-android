package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentSingleTestResultBinding
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.TestRecordDto
import ca.bc.gov.common.utils.toDateTimeString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// fragment initialization parameter
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * @author amit metri
 */
@AndroidEntryPoint
class SingleTestResultFragment : Fragment(R.layout.fragment_single_test_result) {

    private val binding by viewBindings(FragmentSingleTestResultBinding::bind)
    private lateinit var testRecordId: String
    private var testResultId: Long = 0
    private val viewModel: SingleTestResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            testRecordId = it.getString(ARG_PARAM1).toString()
            testResultId = it.getLong(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTestRecordDetail(testRecordId, testResultId)

        observeTestRecordDetails()
    }

    private fun observeTestRecordDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.testRecordDto != null &&
                        state.patientDto != null
                    ) {
                        initUi(state.testRecordDto, state.patientDto)
                    }
                }
            }
        }
    }

    private fun initUi(testRecordDto: TestRecordDto?, patientDto: PatientDto?) {
        binding.apply {
            tvFullName.text = patientDto?.fullName
            tvTestResult.text = testRecordDto?.testOutcome
            tvTestedOn.text =
                getString(R.string.tested_on)
                    .plus(" ")
                    .plus(
                        testRecordDto?.collectionDateTime?.toDateTimeString()
                    )
            tvDot.text = testRecordDto?.collectionDateTime?.toDateTimeString()
            tvTestStatus.text = testRecordDto?.testStatus
            tvTypeName.text = testRecordDto?.testName
            tvProviderClinic.text = testRecordDto?.labName
        }

        if (testRecordDto?.testOutcome == CovidTestResultStatus.Positive.toString() ||
            testRecordDto?.testOutcome == CovidTestResultStatus.Negative.toString() ||
            testRecordDto?.testOutcome == CovidTestResultStatus.Cancelled.toString() ||
            testRecordDto?.testOutcome == CovidTestResultStatus.Indeterminate.toString()
        ) {
            setResultDescription(testRecordDto.resultDescription)
        }

        getCovidTestStatus(testRecordDto)

        binding.scrollView.viewTreeObserver
            .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    binding.scrollView.smoothScrollTo(0, 0)
                }
            })
    }

    private fun setResultDescription(resultDescription: List<String>?) {

        val builder = SpannableStringBuilder(
            resultDescription
                ?.joinToString("\n\n")
                .plus(" ")
                .plus(getString(R.string.understanding_test_results))
        )

        val redirectOnClick = object : ClickableSpan() {
            override fun onClick(view: View) {
                requireContext().redirect(getString(R.string.understanding_test_results_url))
            }
        }

        val totalChars = resultDescription?.joinToString("\n\n")?.length

        totalChars?.let { count ->
            builder.setSpan(
                redirectOnClick,
                count + 1,
                count + 28,
                0
            )
        }

        totalChars?.let { count ->
            builder.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.blue, null)),
                count + 1,
                count + 28,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvResultDesc.visibility = View.VISIBLE
        binding.tvResultDescTitle.visibility = View.VISIBLE
        binding.tvResultDesc.setText(builder, TextView.BufferType.SPANNABLE)
    }

    private fun getCovidTestStatus(
        testRecordDto: TestRecordDto?
    ) {
        when (testRecordDto?.testOutcome) {
            CovidTestResultStatus.Indeterminate.toString() -> {
                setIndeterminateState()
            }
            CovidTestResultStatus.Cancelled.toString() -> {
                setCancelledState(testRecordDto)
            }
            CovidTestResultStatus.Negative.toString() -> {
                setNegativeState()
            }
            CovidTestResultStatus.Positive.toString() -> {
                setPositiveState()
            }
            else -> {
                setPendingState()
            }
        }
    }

    private fun setPendingState() {
        binding.apply {
            tvFullName.visibility = View.GONE
            tvTestResult.visibility = View.GONE
            tvTestedOn.visibility = View.GONE
            tvInfo.visibility = View.VISIBLE
            tvInfo.text = getString(R.string.covid_test_result_pending)
            rectBackground
                .setBackgroundColor(
                    resources.getColor(
                        R.color.covid_test_blue,
                        null
                    )
                )
        }
    }

    private fun setIndeterminateState() {
        binding.apply {
            tvTestResult.setTextColor(
                resources
                    .getColor(R.color.covid_test_text_indeterminate, null)
            )
            rectBackground
                .setBackgroundColor(
                    resources.getColor(
                        R.color.covid_test_blue,
                        null
                    )
                )
        }
    }

    private fun setCancelledState(testRecordDto: TestRecordDto) {
        binding.apply {
            tvFullName.visibility = View.GONE
            tvTestResult.visibility = View.GONE
            tvTestedOn.visibility = View.GONE

            tvInfo.visibility = View.VISIBLE
            val builder = SpannableStringBuilder(getString(R.string.covid_test_result_cancelled))
            val redirectOnClick = object : ClickableSpan() {
                override fun onClick(view: View) {
                    requireContext().redirect(getString(R.string.bc_cdc_test_results))
                }
            }

            builder.setSpan(
                redirectOnClick,
                49,
                68,
                0
            )

            builder.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.blue, null)),
                49,
                68,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvInfo.movementMethod = LinkMovementMethod.getInstance()
            tvInfo.setText(builder, TextView.BufferType.SPANNABLE)

            rectBackground
                .setBackgroundColor(
                    resources.getColor(
                        R.color.covid_test_blue,
                        null
                    )
                )

            tvTestStatus.text = testRecordDto.testOutcome
        }
    }

    private fun setNegativeState() {
        binding.apply {
            tvTestResult.setTextColor(
                resources
                    .getColor(R.color.covid_test_text_negative, null)
            )
            rectBackground
                .setBackgroundColor(
                    resources.getColor(
                        R.color.covid_test_green,
                        null
                    )
                )
        }
    }

    private fun setPositiveState() {
        binding.apply {
            tvTestResult.setTextColor(
                resources
                    .getColor(R.color.covid_test_text_positive, null)
            )
            rectBackground
                .setBackgroundColor(
                    resources.getColor(
                        R.color.covid_test_red,
                        null
                    )
                )
        }
    }

    enum class CovidTestResultStatus {
        Negative,
        Positive,
        Indeterminate,
        Cancelled
    }

    companion object {

        @JvmStatic
        fun newInstance(testRecordId: String, testResultId: Long) =
            SingleTestResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, testRecordId)
                    putLong(ARG_PARAM2, testResultId)
                }
            }
    }
}
