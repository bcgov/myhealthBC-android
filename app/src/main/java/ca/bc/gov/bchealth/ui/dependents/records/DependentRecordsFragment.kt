package ca.bc.gov.bchealth.ui.dependents.records

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentRecordsBinding
import ca.bc.gov.bchealth.ui.dependents.records.filter.DependentFilterViewModel
import ca.bc.gov.bchealth.ui.healthrecord.BaseRecordFilterFragment
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordsAdapter
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.showServiceDownMessage
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentRecordsFragment : BaseRecordFilterFragment(R.layout.fragment_dependent_records) {
    private val binding by viewBindings(FragmentDependentRecordsBinding::bind)
    private val args: DependentRecordsFragmentArgs by navArgs()
    private lateinit var healthRecordsAdapter: HealthRecordsAdapter
    private val viewModel: DependentRecordsViewModel by viewModels()
    private val filterSharedViewModel: DependentFilterViewModel by activityViewModels()

    override fun getFilterViewModel() = filterSharedViewModel
    override fun getFilter() = healthRecordsAdapter.filter
    override fun getLayoutChipGroup() = binding.chipGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        launchOnStart { observeUiState() }
        viewModel.loadRecords(patientId = args.patientId, hdid = args.hdid)

        clearFilterClickListener()
        observeFilterState()
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            binding.apply {
                if (uiState.isHgServicesUp == false) {
                    root.showServiceDownMessage(requireContext())
                    viewModel.resetUiState()
                } else {
                    progressBar.indicator.toggleVisibility(uiState.onLoading)
                    healthRecordsAdapter.setData(uiState.records)
                    healthRecordsAdapter.filter.filter(filterSharedViewModel.getFilterString())
                    rvHealthRecords.setLoading(uiState.onLoading)
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        healthRecordsAdapter = HealthRecordsAdapter {

            val navDirection = when (it.healthRecordType) {
                HealthRecordType.VACCINE_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToVaccineRecordDetailFragment(it.patientId)

                HealthRecordType.COVID_TEST_RECORD ->
                    if (it.covidOrderId != null) {
                        DependentRecordsFragmentDirections.actionDependentRecordsFragmentToCovidTestResultDetailFragment(
                            it.covidOrderId
                        )
                    } else {
                        DependentRecordsFragmentDirections.actionDependentRecordsFragmentToTestResultDetailFragment(
                            it.patientId, it.testResultId
                        )
                    }

                HealthRecordType.MEDICATION_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToMedicationDetailFragment(it.medicationRecordId)

                HealthRecordType.LAB_TEST ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToLabTestDetailFragment(it.labOrderId)

                HealthRecordType.IMMUNIZATION_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToImmunizationRecordDetailFragment(it.immunizationRecordId)

                HealthRecordType.HEALTH_VISIT_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToHealthVisitDetailsFragment(it.healthVisitId)

                HealthRecordType.SPECIAL_AUTHORITY_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToSpecialAuthorityDetailsFragment(it.specialAuthorityId)

                HealthRecordType.HOSPITAL_VISITS_RECORD ->
                    DependentRecordsFragmentDirections
                        .actionDependentRecordsFragmentToHospitalVisitDetailsFragment(it.hospitalVisitId)

                HealthRecordType.CLINICAL_DOCUMENT_RECORD -> null
            }

            navDirection?.let { findNavController().navigate(navDirection) }
        }
        binding.rvHealthRecords.adapter = healthRecordsAdapter
        binding.rvHealthRecords.emptyView = binding.viewEmptyScreen
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        binding.layoutToolbar.apply {
            toolbar.stateListAnimator = null
            toolbar.elevation = 0f

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnProfile.setOnClickListener {
                navigate(
                    R.id.dependentProfileFragment,
                    bundleOf("patient_id" to args.patientId)
                )
            }

            btnFilter.setOnClickListener {
                findNavController().navigate(R.id.dependentFilterFragment)
            }

            tvTitle.text = args.fullName
        }
    }
}
