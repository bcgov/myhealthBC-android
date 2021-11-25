package ca.bc.gov.bchealth.ui.healthrecords.covidtestresults

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentCovidTestResultBinding
import ca.bc.gov.bchealth.ui.healthrecords.covidtestresults.CovidTestResultFragment.CovidTestResult.Cancelled
import ca.bc.gov.bchealth.ui.healthrecords.covidtestresults.CovidTestResultFragment.CovidTestResult.Indeterminate
import ca.bc.gov.bchealth.ui.healthrecords.covidtestresults.CovidTestResultFragment.CovidTestResult.Negative
import ca.bc.gov.bchealth.ui.healthrecords.covidtestresults.CovidTestResultFragment.CovidTestResult.Pending
import ca.bc.gov.bchealth.ui.healthrecords.covidtestresults.CovidTestResultFragment.CovidTestResult.Positive
import ca.bc.gov.bchealth.utils.getDateForCovidTestResults
import ca.bc.gov.bchealth.utils.showHealthRecordDeleteDialog
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CovidTestResultFragment : Fragment(R.layout.fragment_covid_test_result) {

    private val binding by viewBindings(FragmentCovidTestResultBinding::bind)

    private val viewModel: CovidTestResultViewModel by viewModels()

    private val args: CovidTestResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        initUI()
    }

    private fun initUI() {

        val covidTestResult = args.covidTestResult

        binding.apply {
            tvFullName.text = covidTestResult.patientDisplayName
            tvTestResult.text = covidTestResult.testOutcome
            tvTestedOn.text =
                getString(R.string.tested_on)
                    .plus(" ")
                    .plus(
                        covidTestResult.resultDateTime
                            .getDateForCovidTestResults()
                    )

            tvName.text = covidTestResult.patientDisplayName
            tvDot.text = covidTestResult.resultDateTime
                .getDateForCovidTestResults()
            tvTestStatus.text = covidTestResult.testStatus
            tvTestResult2.text = covidTestResult.testOutcome
            tvTypeName.text = covidTestResult.testType
            tvProviderClinic.text = covidTestResult.lab

            when (covidTestResult.testOutcome) {
                Pending.toString() -> {
                    binding.rectBackground
                        .setBackgroundColor(
                            resources.getColor(
                                R.color.covid_test_blue,
                                null
                            )
                        )
                }
                Negative.toString() -> {
                    binding.rectBackground
                        .setBackgroundColor(
                            resources.getColor(
                                R.color.covid_test_green,
                                null
                            )
                        )
                }
                Positive.toString() -> {
                    binding.rectBackground
                        .setBackgroundColor(
                            resources.getColor(
                                R.color.covid_test_red,
                                null
                            )
                        )
                    showInstructions()
                }
                Indeterminate.toString() -> {
                    binding.rectBackground
                        .setBackgroundColor(
                            resources.getColor(
                                R.color.covid_test_blue,
                                null
                            )
                        )
                }
                Cancelled.toString() -> {
                    binding.rectBackground
                        .setBackgroundColor(
                            resources.getColor(
                                R.color.covid_test_blue,
                                null
                            )
                        )
                }
            }
        }
    }

    private fun setToolBar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.visibility = View.VISIBLE
            tvTitle.text = getString(R.string.covid_19_test_result)

            tvRightOption.visibility = View.VISIBLE
            tvRightOption.text = getString(R.string.delete)
            tvRightOption.setOnClickListener {
                requireContext().showHealthRecordDeleteDialog {
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.deleteCovidTestResult(args.covidTestResult.reportId)
                        .invokeOnCompletion {
                            findNavController().popBackStack()
                        }
                }
            }

            line1.visibility = View.VISIBLE
        }
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

        instructions.forEach { item ->
            builder.append(
                item + "\n",
                BulletSpan(30, Color.BLACK),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvInstructions.visibility = View.VISIBLE
        binding.tvInstructionsDetail.visibility = View.VISIBLE
        binding.tvInstructionsDetail.text = builder
    }

    enum class CovidTestResult {
        Pending,
        Negative,
        Positive,
        Indeterminate,
        Cancelled
    }
}
