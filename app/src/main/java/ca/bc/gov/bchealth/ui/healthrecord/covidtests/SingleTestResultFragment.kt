package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentSingleTestResultBinding
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.TestRecord
import ca.bc.gov.common.utils.toDateTimeString
import ca.bc.gov.common.utils.yyyy_MMM_dd_HH_mm

// fragment initialization parameter
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * @author amit metri
 */
class SingleTestResultFragment : Fragment(R.layout.fragment_single_test_result) {

    private val binding by viewBindings(FragmentSingleTestResultBinding::bind)

    private var testRecord: TestRecord? = null

    private var patientDto: PatientDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            testRecord = it.getParcelable(ARG_PARAM1)
            patientDto = it.getParcelable(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvFullName.text = patientDto?.firstName.plus(" ").plus(patientDto?.lastName)
            tvTestResult.text = testRecord?.testOutcome
            tvTestedOn.text =
                getString(R.string.tested_on)
                    .plus(" ")
                    .plus(
                        testRecord?.resultDateTime?.toDateTimeString(yyyy_MMM_dd_HH_mm)
                    )
            tvDot.text = testRecord?.resultDateTime?.toDateTimeString(yyyy_MMM_dd_HH_mm)
            tvTestStatus.text = testRecord?.testStatus
            tvTypeName.text = testRecord?.testName
            tvProviderClinic.text = testRecord?.labName
            if (testRecord?.testOutcome == CovidTestResultStatus.Positive.toString() ||
                testRecord?.testOutcome == CovidTestResultStatus.Negative.toString() ||
                testRecord?.testOutcome == CovidTestResultStatus.Cancelled.toString()
            ) {
                setResultDescription(testRecord?.resultDescription)
            }
        }

        testRecord?.let { getCovidTestStatus(it) }
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
        binding.tvResultDesc.movementMethod = LinkMovementMethod.getInstance()
        binding.tvResultDesc.setText(builder, TextView.BufferType.SPANNABLE)
    }

    private fun getCovidTestStatus(
        testRecord: TestRecord
    ) {
        when (testRecord.testOutcome) {
            CovidTestResultStatus.Indeterminate.toString() -> {
                setIndeterminateState()
            }
            CovidTestResultStatus.Cancelled.toString() -> {
                setCancelledState()
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

    private fun setCancelledState() {
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

            tvTestStatus.text = testRecord?.testOutcome
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
        Cancelled,
        Pending
    }

    companion object {

        @JvmStatic
        fun newInstance(testRecord: TestRecord, patientDto: PatientDto) =
            SingleTestResultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, testRecord)
                    putParcelable(ARG_PARAM2, patientDto)
                }
            }
    }
}
