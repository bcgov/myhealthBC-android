package ca.bc.gov.bchealth.ui.healthrecord

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.ui.healthpass.add.FetchVaccineRecordFragment
import ca.bc.gov.bchealth.ui.healthrecord.add.FetchTestRecordFragment
import ca.bc.gov.common.model.patient.PatientDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
* HealthRecordPlaceholderFragment will act like placeholder fragment.
* It will launch IndividualHealthRecordFragment or healthRecordsFragment or addHealthRecordsFragment
* based on number of patients.
* observeNavigationFlow() will decide whether to recheck the above flow or just navigate back.
* observeNavigationFlow() is required as user will never stay on this fragment.
* */
@AndroidEntryPoint
class HealthRecordPlaceholderFragment : Fragment(R.layout.health_record_placeholder_fragment) {

    private val viewModel: HealthRecordPlaceholderViewModel by viewModels()
    private var isHealthRecordsFlowActive = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isHealthRecordsFlowActive) {
            isHealthRecordsFlowActive = true
            collectHealthRecordsFlow()
        }

        observeVaccineRecordAddition()
        observeCovidTestRecordAddition()
        observeNavigationFlow()
    }

    private fun observeCovidTestRecordAddition() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            FetchTestRecordFragment.TEST_RECORD_ADDED_SUCCESS
        )
            ?.observe(
                viewLifecycleOwner
            ) { recordId ->
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long>(
                    FetchTestRecordFragment.TEST_RECORD_ADDED_SUCCESS
                )
                if (recordId > 0) {
                    collectHealthRecordsFlow()
                }
            }
    }

    private fun observeVaccineRecordAddition() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>(
            FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS
        )?.observe(
            viewLifecycleOwner
        ) {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long>(
                FetchVaccineRecordFragment.VACCINE_RECORD_ADDED_SUCCESS
            )
            if (it > 0) {
                collectHealthRecordsFlow()
            }
        }
    }

    private fun observeNavigationFlow() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<NavigationAction>(
            PLACE_HOLDER_NAVIGATION
        )?.observe(viewLifecycleOwner) {
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<NavigationAction>(
                PLACE_HOLDER_NAVIGATION
            )
            it?.let {
                when (it) {
                    NavigationAction.ACTION_BACK -> {
                        findNavController().popBackStack()
                    }
                    NavigationAction.ACTION_RE_CHECK -> {
                        collectHealthRecordsFlow()
                    }
                }
            }
        }
    }

    private fun collectHealthRecordsFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.patients.collect { records ->
                    navigate(records)
                }
            }
        }
    }

    private fun navigate(records: List<PatientDto>) {
        if (records.isNotEmpty()) {
            if (records.size == 1) {
                val action =
                    HealthRecordPlaceholderFragmentDirections
                        .actionHealthRecordsPlaceHolderFragmentToIndividualHealthRecordFragment(
                            records.first().id,
                            records.first().fullName
                        )
                findNavController().navigate(action)
            } else {
                findNavController().navigate(R.id.healthRecordsFragment)
            }
        } else {
            findNavController().navigate(R.id.addHealthRecordsFragment)
        }
    }

    companion object {
        const val PLACE_HOLDER_NAVIGATION = "PLACE_HOLDER_NAVIGATION"
    }
}

enum class NavigationAction {
    ACTION_BACK,
    ACTION_RE_CHECK
}
