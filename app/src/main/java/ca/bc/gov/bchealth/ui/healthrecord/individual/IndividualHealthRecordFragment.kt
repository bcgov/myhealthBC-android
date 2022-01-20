package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentIndividualHealthRecordBinding
import ca.bc.gov.bchealth.utils.showAlertDialog
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class IndividualHealthRecordFragment : Fragment(R.layout.fragment_individual_health_record) {

    private val binding by viewBindings(FragmentIndividualHealthRecordBinding::bind)
    private val viewModel: IndividualHealthRecordViewModel by viewModels()
    private lateinit var vaccineRecordsAdapter: VaccineRecordsAdapter
    private lateinit var testRecordsAdapter: TestRecordsAdapter
    private lateinit var concatAdapter: ConcatAdapter
    private val args: IndividualHealthRecordFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        setUpRecyclerView()

        setupObserver()

        viewModel.getIndividualsHealthRecord(args.patientId)
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    if (::vaccineRecordsAdapter.isInitialized) {
                        vaccineRecordsAdapter.submitList(uiState.onVaccineRecord)
                    }

                    if (::testRecordsAdapter.isInitialized) {
                        testRecordsAdapter.submitList(uiState.onTestRecords)
                    }

                    if (uiState.onTestRecords.isEmpty() && uiState.onVaccineRecord.isEmpty())
                        findNavController().popBackStack()
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        vaccineRecordsAdapter = VaccineRecordsAdapter(
            { vaccineRecord ->
                val action = IndividualHealthRecordFragmentDirections
                    .actionIndividualHealthRecordFragmentToVaccineRecordDetailFragment(
                        vaccineRecord.patientId
                    )
                findNavController().navigate(action)
            },
            { vaccineRecord ->
                showHealthRecordDeleteDialog(vaccineRecord)
            }
        )

        testRecordsAdapter = TestRecordsAdapter(
            { testResult ->
                val action = IndividualHealthRecordFragmentDirections
                    .actionIndividualHealthRecordFragmentToTestResultDetailFragment(
                        testResult.patientId,
                        testResult.testResultId
                    )
                findNavController().navigate(action)
            },
            { testResult ->
                showHealthRecordDeleteDialog(testResult)
            }
        )

        concatAdapter = ConcatAdapter(vaccineRecordsAdapter, testRecordsAdapter)
        binding.rvHealthRecords.adapter = concatAdapter
        binding.rvHealthRecords.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            ivLeftOption.visibility = View.VISIBLE
            ivLeftOption.setImageResource(R.drawable.ic_action_back)
            ivLeftOption.setOnClickListener {
                findNavController().popBackStack()
            }
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = args.patientName.plus("'s record")
            line1.visibility = View.VISIBLE

            binding.toolbar.tvRightOption.apply {
                visibility = View.VISIBLE
                text = getString(R.string.edit)
                setOnClickListener {
                    if (vaccineRecordsAdapter.canDeleteRecord) {
                        text = getString(R.string.edit)
                        vaccineRecordsAdapter.canDeleteRecord = false
                    } else {
                        text = getString(R.string.done)
                        vaccineRecordsAdapter.canDeleteRecord = true
                    }

                    if (testRecordsAdapter.canDeleteRecord) {
                        text = getString(R.string.edit)
                        testRecordsAdapter.canDeleteRecord = false
                    } else {
                        text = getString(R.string.done)
                        testRecordsAdapter.canDeleteRecord = true
                    }

                    concatAdapter.notifyItemRangeChanged(
                        0,
                        concatAdapter.itemCount
                    )
                }
            }
        }
    }

    private fun showHealthRecordDeleteDialog(healthRecordItem: HealthRecordItem) {

        when (healthRecordItem.healthRecordType) {

            HealthRecordType.VACCINE_RECORD -> {
                requireContext().showAlertDialog(
                    title = getString(R.string.delete_hc_record_title),
                    message = getString(R.string.delete_individual_vaccine_record_message),
                    positiveButtonText = getString(R.string.delete),
                    negativeButtonText = getString(R.string.not_now)
                ) {
                    viewModel.deleteVaccineRecord(
                        healthRecordItem.patientId
                    ).invokeOnCompletion { viewModel.getIndividualsHealthRecord(args.patientId) }
                }
            }

            HealthRecordType.COVID_TEST_RECORD -> {
                requireContext().showAlertDialog(
                    title = getString(R.string.delete_hc_record_title),
                    message = getString(R.string.delete_individual_covid_test_record_message),
                    positiveButtonText = getString(R.string.delete),
                    negativeButtonText = getString(R.string.not_now)
                ) {
                    viewModel.deleteTestRecord(
                        healthRecordItem.testResultId
                    ).invokeOnCompletion { viewModel.getIndividualsHealthRecord(args.patientId) }
                }
            }
        }
    }
}
