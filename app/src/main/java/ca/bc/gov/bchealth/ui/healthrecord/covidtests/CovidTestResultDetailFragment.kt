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
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.test.CovidOrderWithCovidTestAndPatientDto
import ca.bc.gov.common.model.test.CovidTestDto
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author amit metri
 */
@AndroidEntryPoint
class CovidTestResultDetailFragment : Fragment(R.layout.fragment_test_result_detail) {

    private val binding by viewBindings(FragmentTestResultDetailBinding::bind)

    private val viewModel: CovidTestResultDetailsViewModel by viewModels()

    private val args: CovidTestResultDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        viewModel.getCovidOrderWithCovidTests(args.covidOrderId)

        observeDetails()
    }

    private fun observeDetails() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->

                    binding.progressBar.isVisible = state.onLoading

                    state.onCovidTestResultDetail.let { covidTestResult ->

                        if (covidTestResult != null) {
                            initUi(
                                covidTestResult
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initUi(covidTestResult: CovidOrderWithCovidTestAndPatientDto) {

        binding.toolbar.tvRightOption.isVisible =
            covidTestResult.patient.authenticationStatus != AuthenticationStatus.AUTHENTICATED
        val covidTestResultsAdapter = CovidTestResultsAdapter(
            this,
            covidTestResult.covidOrderWithCovidTest.covidTests
        )

        binding.viewpagerCovidTestResults.adapter = covidTestResultsAdapter

        if (covidTestResult.covidOrderWithCovidTest.covidTests.size > 1) {

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
            line1.visibility = View.VISIBLE
        }
    }

    inner class CovidTestResultsAdapter(
        fragment: Fragment,
        private val covidTests: List<CovidTestDto>
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = covidTests.size

        override fun createFragment(position: Int): Fragment {

            return CovidTestResultFragment.newInstance(
                args.covidOrderId,
                covidTests[position].id
            )
        }
    }
}
