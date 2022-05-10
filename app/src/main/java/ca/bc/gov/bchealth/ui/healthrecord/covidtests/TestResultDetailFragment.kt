package ca.bc.gov.bchealth.ui.healthrecord.covidtests

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.viewpager2.adapter.FragmentStateAdapter
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentTestResultDetailBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.healthrecord.add.FetchTestRecordFragment
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.patient.PatientDto
import ca.bc.gov.common.model.test.TestRecordDto
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author amit metri
 */
@AndroidEntryPoint
class TestResultDetailFragment : BaseFragment(R.layout.fragment_test_result_detail) {

    private val binding by viewBindings(FragmentTestResultDetailBinding::bind)

    private val viewModel: TestResultDetailsViewModel by viewModels()

    private val args: TestResultDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTestResultDetail(args.testResultId)
        observeDetails()
    }

    private fun observeDetails() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->

                    binding.progressBar.isVisible = state.onLoading

                    state.onTestResultDetail.let { patientTestResult ->

                        if (patientTestResult != null) {
                            initUi(
                                patientTestResult.testResultWithRecords.testRecords,
                                patientTestResult.patient
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initUi(testRecords: List<TestRecordDto>, patientDto: PatientDto) {

        menuInflated.getItem(0).isVisible =
            patientDto.authenticationStatus != AuthenticationStatus.AUTHENTICATED
        val covidTestResultsAdapter = CovidTestResultsAdapter(
            this,
            testRecords
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

    private lateinit var menuInflated: Menu
    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                if (findNavController().previousBackStackEntry?.destination?.id ==
                    R.id.fetchTestRecordFragment
                ) {
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(
                            FetchTestRecordFragment.TEST_RECORD_ADDED_SUCCESS,
                            args.testResultId
                        )
                }
                findNavController().popBackStack()
            }
            title = getString(R.string.covid_19_test_result)
            inflateMenu(R.menu.menu_details_page_delete)
            menuInflated = menu
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menu_delete -> {
                        AlertDialogHelper.showAlertDialog(
                            context = requireContext(),
                            title = getString(R.string.delete_hc_record_title),
                            msg = getString(R.string.delete_individual_covid_test_record_message),
                            positiveBtnMsg = getString(R.string.delete),
                            negativeBtnMsg = getString(R.string.not_now),
                            positiveBtnCallback = {
                                binding.progressBar.visibility = View.VISIBLE
                                viewModel.deleteTestRecord(args.testResultId)
                                    .invokeOnCompletion {
                                        findNavController().popBackStack()
                                    }
                            }
                        )
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    inner class CovidTestResultsAdapter(
        fragment: Fragment,
        private val testRecordDtos: List<TestRecordDto>
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = testRecordDtos.size

        override fun createFragment(position: Int): Fragment {

            return SingleTestResultFragment.newInstance(
                testRecordDtos[position].id,
                args.testResultId
            )
        }
    }
}
