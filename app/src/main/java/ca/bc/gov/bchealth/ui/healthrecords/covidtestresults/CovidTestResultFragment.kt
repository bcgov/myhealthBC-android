package ca.bc.gov.bchealth.ui.healthrecords.covidtestresults

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.databinding.FragmentCovidTestResultBinding
import ca.bc.gov.bchealth.utils.showAlertDialog
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CovidTestResultFragment : Fragment(R.layout.fragment_covid_test_result) {

    private val binding by viewBindings(FragmentCovidTestResultBinding::bind)

    private val viewModel: CovidTestResultViewModel by viewModels()

    private val args: CovidTestResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar()

        initUi()
    }

    private fun initUi() {

        val covidTestResultsAdapter = CovidTestResultsAdapter(
            this,
            args.covidTestResultList.toList()
        )

        binding.viewpagerCovidTestResults.adapter = covidTestResultsAdapter

        if (args.covidTestResultList.size > 1) {

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
                    viewModel.deleteCovidTestResult(args.covidTestResultList.first().combinedReportId)
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
        val covidTestResults: List<CovidTestResult>
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = covidTestResults.size

        override fun createFragment(position: Int): Fragment {

            return ItemCovidTestResultFragment.newInstance(covidTestResults[position])
        }
    }
}
