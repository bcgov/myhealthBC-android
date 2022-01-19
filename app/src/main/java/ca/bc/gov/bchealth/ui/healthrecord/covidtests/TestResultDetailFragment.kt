package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentTestResultDetailBinding
import ca.bc.gov.bchealth.utils.showAlertDialog
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.patient.Patient
import ca.bc.gov.common.model.test.TestRecord
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author amit metri
 */
@AndroidEntryPoint
class TestResultDetailFragment : Fragment(R.layout.fragment_test_result_detail) {

    private val binding by viewBindings(FragmentTestResultDetailBinding::bind)

    private val viewModel: TestResultDetailsViewModel by viewModels()

    private val args: TestResultDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        viewModel.getTestResultDetail(args.patientId, args.testResultId)

        observeDetails()
    }

    private fun observeDetails() {

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->

                    binding.progressBar.isVisible = state.onLoading

                    state.onTestResultDetail.let { patientTestResult ->

                        patientTestResult?.records?.let { initUi(it, patientTestResult.patient) }
                    }
                }
            }
        }
    }

    private fun initUi(testRecords: List<TestRecord>, patient: Patient) {

        val covidTestResultsAdapter = CovidTestResultsAdapter(
            this,
            testRecords,
            patient
        )

        binding.viewpagerCovidTestResults.adapter = covidTestResultsAdapter

        if (testRecords.size > 1) {

            binding.tabCovidTestResults.visibility = View.VISIBLE

            TabLayoutMediator(
                binding.tabCovidTestResults,
                binding.viewpagerCovidTestResults
            ) { _, _ -> }.attach()
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
                requireContext().showAlertDialog(
                    title = getString(R.string.delete_hc_record_title),
                    message = getString(R.string.delete_individual_covid_test_record_message),
                    positiveButtonText = getString(R.string.delete),
                    negativeButtonText = getString(R.string.not_now)
                ) {
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.deleteTestRecord(args.testResultId)
                        .invokeOnCompletion {
                            findNavController().popBackStack()
                        }
                }
            }

            line1.visibility = View.VISIBLE
        }
    }

    class CovidTestResultsAdapter(
        fragment: Fragment,
        private val testRecords: List<TestRecord>,
        private val patient: Patient
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = testRecords.size

        override fun createFragment(position: Int): Fragment {

            return SingleTestResultFragment.newInstance(testRecords[position], patient)
        }
    }
}
