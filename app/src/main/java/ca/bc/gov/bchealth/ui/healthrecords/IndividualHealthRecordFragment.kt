package ca.bc.gov.bchealth.ui.healthrecords

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.data.local.entity.CovidTestResult
import ca.bc.gov.bchealth.databinding.FragmentIndividualHealthRecordBinding
import ca.bc.gov.bchealth.model.healthrecords.IndividualRecord
import ca.bc.gov.bchealth.model.healthrecords.toHealthRecord
import ca.bc.gov.bchealth.repository.HealthRecordType
import ca.bc.gov.bchealth.utils.showAlertDialog
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IndividualHealthRecordFragment : Fragment(R.layout.fragment_individual_health_record) {

    private val binding by viewBindings(FragmentIndividualHealthRecordBinding::bind)

    private val args: IndividualHealthRecordFragmentArgs by navArgs()

    private lateinit var individualHealthRecordAdapter: IndividualHealthRecordAdapter

    private val viewModel: IndividualHealthRecordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        setUpRecyclerView()

        fetchUpdatedHealthRecords()
    }

    // Toolbar setup
    private fun setupToolBar() {

        binding.toolbar.ivLeftOption.visibility = View.VISIBLE
        binding.toolbar.ivLeftOption.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.tvTitle.visibility = View.VISIBLE
        binding.toolbar.tvTitle.isSelected = true
        binding.toolbar.tvTitle.text = args.healthRecord.name.plus(
            getString(R.string.member_records_toolbar_title)
        )

        binding.toolbar.tvRightOption.apply {
            visibility = View.VISIBLE
            text = getString(R.string.edit)
            setOnClickListener {
                if (individualHealthRecordAdapter.canDeleteRecord) {
                    text = getString(R.string.edit)
                    individualHealthRecordAdapter.canDeleteRecord = false
                } else {
                    text = getString(R.string.done)
                    individualHealthRecordAdapter.canDeleteRecord = true
                }
                individualHealthRecordAdapter.notifyItemRangeChanged(
                    0,
                    individualHealthRecordAdapter.itemCount
                )
            }
        }
    }

    private fun fetchUpdatedHealthRecords() {

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.healthRecords.collect { healthRecords ->

                    healthRecords?.let { individualHealthRecord ->

                        if (individualHealthRecord.isEmpty()) {
                            findNavController().popBackStack()
                        }

                        val records = individualHealthRecord.filter { individualRecord ->
                            individualRecord.name.lowercase() == args.healthRecord.name.lowercase()
                        }

                        if (records.isEmpty()) {
                            findNavController().popBackStack()
                        }
                        individualHealthRecordAdapter.individualRecords = records.toMutableList()
                        individualHealthRecordAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    // Recycler view setup
    private fun setUpRecyclerView() {

        individualHealthRecordAdapter = IndividualHealthRecordAdapter(
            mutableListOf(),
            false,
            onItemClickListener = { individualRecord ->

                when (individualRecord.healthRecordType) {

                    HealthRecordType.COVID_TEST_RECORD -> {
                        navigateToCovidTestResultPage(
                            individualRecord.covidTestResultList
                        )
                    }

                    HealthRecordType.VACCINE_RECORD -> {
                        val action = IndividualHealthRecordFragmentDirections
                            .actionIndividualHealthRecordFragmentToVaccineDetailsFragment(
                                individualRecord.toHealthRecord()
                            )
                        findNavController().navigate(action)
                    }
                }
            }, onDeleteListener = { individualRecord ->
            showHealthRecordDeleteDialog(individualRecord)
        }
        )

        val recyclerView = binding.rvHealthRecords

        recyclerView.adapter = individualHealthRecordAdapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showHealthRecordDeleteDialog(individualRecord: IndividualRecord) {

        when (individualRecord.healthRecordType) {

            HealthRecordType.COVID_TEST_RECORD -> {
                requireContext().showAlertDialog(
                    title = getString(R.string.delete_hc_record_title),
                    message = getString(R.string.delete_individual_covid_test_record_message),
                    positiveButtonText = getString(R.string.delete),
                    negativeButtonText = getString(R.string.not_now)
                ) {
                    viewModel.deleteCovidTestResult(
                        individualRecord.covidTestResultList.first()
                            .combinedReportId
                    )
                }
            }

            HealthRecordType.VACCINE_RECORD -> {
                requireContext().showAlertDialog(
                    title = getString(R.string.delete_hc_record_title),
                    message = getString(R.string.delete_individual_vaccine_record_message),
                    positiveButtonText = getString(R.string.delete),
                    negativeButtonText = getString(R.string.not_now)
                ) {
                    viewModel.deleteVaccineRecord(individualRecord.healthPassId)
                }
            }
        }
    }

    private fun navigateToCovidTestResultPage(covidTestResults: List<CovidTestResult>) {

        val action = IndividualHealthRecordFragmentDirections
            .actionIndividualHealthRecordFragmentToCovidTestResultFragment(
                covidTestResults.toTypedArray()
            )
        findNavController().navigate(action)
    }
}
