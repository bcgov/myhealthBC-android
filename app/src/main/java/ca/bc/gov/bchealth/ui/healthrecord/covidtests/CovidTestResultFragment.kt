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
import ca.bc.gov.bchealth.model.mapper.CovidTestResultStatus
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.showIfNullOrBlank
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.CovidOrderDto
import ca.bc.gov.common.model.test.CovidTestDto
import ca.bc.gov.common.utils.toDateTimeString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val COVID_ORDER_ID = "COVID_ORDER_ID"
private const val COVID_TEST_ID = "COVID_TEST_ID"

/**
 * @author amit metri
 */
@AndroidEntryPoint
class CovidTestResultFragment : Fragment(R.layout.fragment_single_test_result) {

    private val binding by viewBindings(FragmentSingleTestResultBinding::bind)
    private lateinit var covidOrderId: String
    private lateinit var covidTestId: String
    private val viewModel: CovidTestResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            covidOrderId = it.getString(COVID_ORDER_ID).toString()
            covidTestId = it.getString(COVID_TEST_ID).toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCovidTestDetail(covidOrderId, covidTestId)

        observeTestRecordDetails()
    }

    private fun observeTestRecordDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.covidTest != null &&
                        state.patient != null && state.covidOrder != null
                    ) {
                        initUi(state.covidOrder, state.covidTest, state.patient)
                    }
                }
            }
        }
    }

    private fun initUi(
        covidOrder: CovidOrderDto,
        covidTest: CovidTestDto?,
        patientDto: PatientDto?
    ) {
        binding.apply {
            tvFullName.text = patientDto?.fullName
            tvTestResult.text = covidTest?.labResultOutcome
            tvTestedOn.text =
                getString(R.string.tested_on)
                    .plus(" ")
                    .plus(
                        covidTest?.collectedDateTime?.toDateTimeString()
                    )
            tvDot.text =
                covidTest?.collectedDateTime?.toDateTimeString().showIfNullOrBlank(requireContext())
            tvTestStatus.text = covidTest?.testStatus.showIfNullOrBlank(requireContext())
            tvTypeName.text = covidTest?.testType.showIfNullOrBlank(requireContext())
            tvProviderClinic.text = covidOrder.orderingProviders.showIfNullOrBlank(requireContext())
        }

        if (covidTest?.labResultOutcome == CovidTestResultStatus.Positive.toString() ||
            covidTest?.labResultOutcome == CovidTestResultStatus.Negative.toString() ||
            covidTest?.labResultOutcome == CovidTestResultStatus.Cancelled.toString() ||
            covidTest?.labResultOutcome == CovidTestResultStatus.Indeterminate.toString()
        ) {
            setResultDescription(covidTest.resultDescription)
        }

        getCovidTestStatus(covidTest)

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
        covidTest: CovidTestDto?
    ) {

        if (covidTest?.testStatus == CovidTestResultStatus.Pending.name) {
            setPendingState()
        } else {
            when (covidTest?.labResultOutcome) {
                CovidTestResultStatus.Indeterminate.name,
                CovidTestResultStatus.IndeterminateResult.name -> {
                    setIndeterminateState()
                }
                CovidTestResultStatus.Cancelled.name -> {
                    setCancelledState(covidTest)
                }
                CovidTestResultStatus.Negative.name -> {
                    setNegativeState()
                }
                CovidTestResultStatus.Positive.name -> {
                    setPositiveState()
                }
                else -> {
                    setIndeterminateState()
                }
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
            tvTestResult.text = CovidTestResultStatus.Indeterminate.name
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

    private fun setCancelledState(covidTest: CovidTestDto) {
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

            tvTestStatus.text = covidTest.labResultOutcome
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

    companion object {

        @JvmStatic
        fun newInstance(covidOrderId: String, covidTestId: String) =
            CovidTestResultFragment().apply {
                arguments = Bundle().apply {
                    putString(COVID_ORDER_ID, covidOrderId)
                    putString(COVID_TEST_ID, covidTestId)
                }
            }
    }
}
