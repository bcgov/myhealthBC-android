package ca.bc.gov.bchealth.ui.dependents.records

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentDependentRecordsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordType
import ca.bc.gov.bchealth.ui.healthrecord.individual.HealthRecordsAdapter
import ca.bc.gov.bchealth.ui.healthrecord.individual.IndividualHealthRecordFragmentDirections
import ca.bc.gov.bchealth.utils.launchOnStart
import ca.bc.gov.bchealth.utils.toggleVisibility
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependentRecordsFragment : BaseFragment(R.layout.fragment_dependent_records) {
    private val binding by viewBindings(FragmentDependentRecordsBinding::bind)
    private val args: DependentRecordsFragmentArgs by navArgs()
    private lateinit var healthRecordsAdapter: HealthRecordsAdapter
    private val viewModel: DependentRecordsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        launchOnStart { observeUiState() }
        viewModel.loadRecords(patientId = args.patientId, hdid = args.hdid)
    }

    private suspend fun observeUiState() {
        viewModel.uiState.collect { uiState ->
            binding.apply {
                progressBar.indicator.toggleVisibility(uiState.onLoading)
            }
        }
    }

    // if (::healthRecordsAdapter.isInitialized) {
    //     healthRecordsAdapter.setData(
    //         listOf(
    //             HealthRecordItem(
    //                 patientId = 1,
    //                 testResultId = 1,
    //                 medicationRecordId = 1,
    //                 labOrderId = 1,
    //                 immunizationRecordId = 1,
    //                 covidOrderId = "1",
    //                 healthVisitId = 1,
    //                 specialAuthorityId = 1,
    //                 icon = R.drawable.ic_banner_icon,
    //                 title = "title",
    //                 description = "description",
    //                 testOutcome = "oytcome",
    //                 date = Instant.now(),
    //                 healthRecordType = HealthRecordType.HEALTH_VISIT_RECORD,
    //                 dataSource = "source",
    //             )
    //         )
    //     )
    // }

    private fun setUpRecyclerView() {
        healthRecordsAdapter = HealthRecordsAdapter {
            when (it.healthRecordType) {
                HealthRecordType.VACCINE_RECORD -> {
                    val action = IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToVaccineRecordDetailFragment(
                            it.patientId
                        )
                    findNavController().navigate(action)
                }
                HealthRecordType.COVID_TEST_RECORD -> {

                    val action = if (it.covidOrderId != null) {
                        IndividualHealthRecordFragmentDirections.actionIndividualHealthRecordFragmentToCovidTestResultDetailFragment(
                            it.covidOrderId
                        )
                    } else {
                        IndividualHealthRecordFragmentDirections
                            .actionIndividualHealthRecordFragmentToTestResultDetailFragment(
                                it.patientId,
                                it.testResultId
                            )
                    }
                    findNavController().navigate(action)
                }
                HealthRecordType.MEDICATION_RECORD -> {
                    val action = IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToMedicationDetailFragment(
                            it.medicationRecordId
                        )
                    findNavController().navigate(action)
                }
                HealthRecordType.LAB_TEST -> {
                    it.labOrderId.let { it1 ->
                        val action = IndividualHealthRecordFragmentDirections
                            .actionIndividualHealthRecordFragmentToLabTestDetailFragment(
                                it1
                            )
                        findNavController().navigate(action)
                    }
                }
                HealthRecordType.IMMUNIZATION_RECORD -> {
                    val action = IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToImmunizationRecordDetailFragment(
                            it.immunizationRecordId
                        )
                    findNavController().navigate(action)
                }
                HealthRecordType.HEALTH_VISIT_RECORD -> {
                    val action = IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToHealthVisitDetailsFragment(
                            it.healthVisitId
                        )
                    findNavController().navigate(action)
                }
                HealthRecordType.SPECIAL_AUTHORITY_RECORD -> {
                    val action = IndividualHealthRecordFragmentDirections
                        .actionIndividualHealthRecordFragmentToSpecialAuthorityDetailsFragment(
                            it.specialAuthorityId
                        )
                    findNavController().navigate(action)
                }
            }
        }
        binding.rvHealthRecords.adapter = healthRecordsAdapter
    }

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        binding.layoutToolbar.apply {
            toolbar.stateListAnimator = null
            toolbar.elevation = 0f

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvTitle.text = args.fullName
        }
    }
}
