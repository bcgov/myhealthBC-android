package ca.bc.gov.bchealth.ui.healthrecords.covidtestresults

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.BulletSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.databinding.ItemCovidTestResultBinding
import ca.bc.gov.bchealth.utils.getDateForCovidTestResults
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings

// fragment initialization parameter
private const val ARG_PARAM1 = "param1"

/*
* @author amit_metri on 20,December,2021
*/
class ItemCovidTestResultFragment : Fragment(R.layout.item_covid_test_result) {

    private val binding by viewBindings(ItemCovidTestResultBinding::bind)

    private var covidTestResult: CovidTestResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            covidTestResult = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvFullName.text = covidTestResult?.patientDisplayName
            tvTestResult.text = covidTestResult?.testOutcome
            tvTestedOn.text =
                getString(R.string.tested_on)
                    .plus(" ")
                    .plus(
                        covidTestResult?.resultDateTime
                            ?.getDateForCovidTestResults()
                    )
            tvDot.text = covidTestResult?.resultDateTime
                ?.getDateForCovidTestResults()
            tvTestStatus.text = covidTestResult?.testStatus
            tvTypeName.text = covidTestResult?.testName
            tvProviderClinic.text = covidTestResult?.lab
        }

        covidTestResult?.let { getCovidTestStatus(it) }
    }

    private fun getCovidTestStatus(
        covidTestResult: CovidTestResult
    ) {
        when (covidTestResult.testOutcome) {
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
            tvInfo.text = getString(R.string.covid_test_result_cancelled)
            rectBackground
                .setBackgroundColor(
                    resources.getColor(
                        R.color.covid_test_blue,
                        null
                    )
                )

            tvTestStatus.text = covidTestResult?.testOutcome
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

        showInstructions()
    }

    private fun showInstructions() {

        val instructions = listOf(
            getString(R.string.instruction_1),
            getString(R.string.instruction_2),
            getString(R.string.instruction_3),
            getString(R.string.instruction_4),
            getString(R.string.instruction_5)
        )

        val builder = SpannableStringBuilder()

        instructions.forEachIndexed { index, item ->

            builder.append(
                item + "\n",
                BulletSpan(30, Color.BLACK),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (index == 4) {

                setupDialer(builder)

                setUpRedirection(builder)
            }
        }

        binding.tvInstructions.visibility = View.VISIBLE
        binding.tvInstructionsDetail.visibility = View.VISIBLE
        binding.tvInstructionsDetail.movementMethod = LinkMovementMethod.getInstance()
        binding.tvInstructionsDetail.setText(builder, TextView.BufferType.SPANNABLE)
    }

    private fun setUpRedirection(builder: SpannableStringBuilder) {
        val redirectOnClick = object : ClickableSpan() {
            override fun onClick(view: View) {
                requireContext().redirect(getString(R.string.understanding_test_results))
            }
        }

        builder.setSpan(
            redirectOnClick,
            269,
            295,
            0
        )

        builder.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.blue, null)),
            269,
            295,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun setupDialer(builder: SpannableStringBuilder) {
        val dialOnClick = object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:811")
                startActivity(intent)
            }
        }

        builder.setSpan(
            dialOnClick,
            215,
            220,
            0
        )

        builder.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.blue, null)),
            215,
            220,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    companion object {
        /**
         *
         * @param param1 position from view pager's adapter.
         */
        @JvmStatic
        fun newInstance(param1: CovidTestResult) =
            ItemCovidTestResultFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }

    enum class CovidTestResultStatus {
        Negative,
        Positive,
        Indeterminate,
        Cancelled
    }
}
